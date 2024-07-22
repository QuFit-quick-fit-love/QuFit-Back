package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.repository.ChatRoomMemberRepository;
import com.cupid.qufit.domain.chat.repository.ChatRoomRepository;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.domain.member.repository.MemberRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WebSocketChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * * 채팅 메시지 전송 처리 ! : 1:1만 고려
     * <p>
     * ! : 메시지 발신자는 항상 "/pub" prefix를 고정적으로 사용해야함.
     *
     * @param chatRoomId 채팅방 ID
     * @return 저장 및 전송된 채팅 메시지
     * @Param chatMessage 전송할 채팅 메시지
     */

    @MessageMapping("/chat.sendMessage/{chatRoomId}") // ! -> 전체 경로 : /pub/chat/message
    @SendTo("/sub/chatroom.{chatRoomId}") // ! 처리된 메시지를 발송할 구독 주제. (어느 목적지로 보낼 지 지정)
    public ChatMessage sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                                   @Payload ChatMessage chatMessage) {
        log.info("메시지 수신 : {}", chatMessage);
        return chatService.processChatMessage(chatRoomId, chatMessage);
    }


    /**
     * * 세션 종료 시 처리
     */
    @MessageMapping("/chat.sessionClose")
    public void handleSessionClose(@Payload Long memberId) {
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findByMemberId(memberId);
        for (ChatRoomMember member : chatRoomMembers) {
            ChatRoom chatRoom = member.getChatRoom();
            member.setLastReadMessageId(chatRoom.getLastMessageId());
            member.setLastReadTime(chatRoom.getLastMessageTime());
            chatRoomMemberRepository.save(member);
        }
    }

    /**
     * * 메시지를 읽었을 때의 상황(채팅방 들어옴) : 해당 사용자의 lastReadMessagId 업데이트, unreadCount 0 으로 갱신 *
     * <p>
     * * 갱신된 ChatRoomDTO 전송
     * TODO : 나중에 security, JWT 도입 후 멤버 뽑아내서 사용
     */
    @MessageMapping("/chat.markAsRead/{chatRoomId}")
    public void markAsRead(@DestinationVariable("chatRoomId") Long chatRoomId,
                           @Header("memberId") Long memberId) {
        Member currentMember = memberRepository.findById(memberId)
                                               .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                                              .orElseThrow(() -> new EntityNotFoundException("채팅방을 찾을 수 없습니다."));

        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, currentMember)
                                                                .orElseThrow(() -> new EntityNotFoundException(
                                                                        "채팅방 멤버 정보가 없습니다."));

        chatRoomMember.setLastReadMessageId(chatRoom.getLastMessageId());
        chatRoomMember.setUnreadCount(0);
        chatRoomMember.setLastReadTime(LocalDateTime.now());
        chatRoomMemberRepository.save(chatRoomMember);

        ChatRoomDTO updatedDTO = ChatRoomDTO.from(chatRoom, chatRoomMember, chatRoom.getOtherMember(currentMember));

        messagingTemplate.convertAndSend("/sub/chatroom-list." + memberId, updatedDTO);
    }

    /**
     * * 특정 채팅방 들어올 때
     *
     * ! 읽지 않은 메시지 카운트 초기화 , 최근 메시지 로딩
     */
    @MessageMapping("/chat.enterRoom/{chatRoomId}")
    public void enterChatRoom(@DestinationVariable("chatRoomId") Long chatRoomId, @Header("memberId") Long memberId,
                              @Payload
                              Pageable pageable) {
        // ! 1. 채팅방에 들어가면서 unreadCount 0으로 갱신
//        chatService.resetUnreadCount(chatRoomId, memberId);

        // ! 2. 메시지 목록 조회
        ChatRoomMessageResponse result = chatService.getChatRoomMessages(chatRoomId, memberId, pageable);

        // ! 3. 결과를 클라이언트로 전송
        messagingTemplate.convertAndSend("/sub/chatroom.messages." + chatRoomId + "." + memberId, result);

    }

    /**
     * * 채팅방 나갈 때
     */
    @MessageMapping("/chat.leaveRoom/{chatRoomId}")
    public void leaveChatRoom(@DestinationVariable("chatRoomId") Long chatRoomId, @Header("memberId") Long memberId) {
        // ! 1. 마지막으로 읽은 메시지 ID 갱신
    }

}
