package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.repository.ChatRoomMemberRepository;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import com.cupid.qufit.global.common.request.MessagePaginationRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ChatRoomMemberRepository chatRoomMemberRepository;
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
     * * 특정 채팅방 들어올 때
     * <p>
     * ! 읽지 않은 메시지 카운트 초기화 , 최근 메시지 로딩
     */
    @MessageMapping("/chat.enterRoom/{chatRoomId}")
    public void enterChatRoom(@DestinationVariable Long chatRoomId, @Payload MessagePaginationRequest pageRequest) {
        Long memberId = 4L;
        Pageable pageable = PageRequest.of(0, pageRequest.getPageSize(), Sort.by(Sort.Direction.ASC, "timestamp"));
        ChatRoomMessageResponse response = chatService.enterChatRoom(chatRoomId, memberId, pageable);
        messagingTemplate.convertAndSend("/sub/chatroom." + chatRoomId + "." + memberId, response);
    }

    /**
     * * 채팅방 나갈 때
     */
    @MessageMapping("/chat.leaveRoom/{chatRoomId}")
    public void leaveChatRoom(@DestinationVariable("chatRoomId") Long chatRoomId, @Header("memberId") Long memberId) {
        // ! 1. 마지막으로 읽은 메시지 ID 갱신
    }

    /**
     * * 위로 스크롤 (이전 메시지 불러오기)
     * <p>
     * ! 프론트에서 보유한 가장 최신 메시지ID로부터 위로 20개 로딩
     * ! 특정 사용자에게 전송해야 하므로 convertAndSendToUser 메서드를 활용
     */
    @MessageMapping("/chat.loadPreviousMessages/{chatRoomId}")
    public void loadPreviousMessages(@DestinationVariable Long chatRoomId, @Payload MessagePaginationRequest messagePaginationRequest) {
        Long memberId = 3L; // ! 일단 하드코딩 -> 이후에 JWT 토큰 반영
        ChatRoomMessageResponse response = chatService.loadPreviousMessages(chatRoomId, memberId,
                                                                            messagePaginationRequest);
        log.info("response : {}", response);

        messagingTemplate.convertAndSendToUser(memberId.toString(), "/sub/chat.messages." + chatRoomId, response);

    }

}
