package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatMessageDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.repository.ChatRoomMemberRepository;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import com.cupid.qufit.global.common.request.MessagePaginationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
@Transactional
@Slf4j
@Tag(name = "Chat-WebSocket", description = "채팅 관련 웹소켓 통신 API")
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
     * <p>
     * ! 특정 채팅방 들어가는 것은 개인화된 반응 -> convertAndSendToUser로 변경.
     *
     * @return
     */
    @MessageMapping("/chat.enterRoom/{chatRoomId}")
    public void enterChatRoom(@DestinationVariable Long chatRoomId,
                              @Payload MessagePaginationRequest pageRequest,
                              SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = Long.parseLong(headerAccessor.getSessionAttributes().get("AUTHENTICATED_MEMBER_ID").toString());
        log.info("현재 회원의 ID = {}", memberId);
        ChatRoomMessageResponse response = chatService.enterChatRoom(chatRoomId, memberId,
                                                                                    pageRequest.getPageSize());
        messagingTemplate.convertAndSendToUser(memberId.toString(), "/sub/chatroom." + chatRoomId, response);
    }

    /**
     * * 채팅방 나갈 때
     * <p>
     * ! 해당 채팅방의 메시지 구독 해제
     */
    @MessageMapping("/chat.leaveRoom/{chatRoomId}")
    public void leaveChatRoom(@DestinationVariable("chatRoomId") Long chatRoomId,
                              @Payload ChatMessageDTO lastMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = Long.parseLong(headerAccessor.getSessionAttributes().get("AUTHENTICATED_MEMBER_ID").toString());
        chatService.updateChatRoomMember(chatRoomId, memberId, lastMessage);
    }

    /**
     * * 위로 스크롤 (이전 메시지 불러오기)
     * <p>
     * ! 프론트에서 보유한 가장 최신 메시지ID로부터 위로 20개 로딩
     * <p>
     * ! 특정 사용자에게 전송해야 하므로 convertAndSendToUser 메서드를 활용
     * <p>
     * ! convertAndSendToUser는 기본으로 '/user' 가 추가됨 -> 컨벤션 해당 값을 prefix로 해서 수신해야함.
     */
    @MessageMapping("/chat.loadPreviousMessages/{chatRoomId}")
    public void loadPreviousMessages(@DestinationVariable Long chatRoomId,
                                     @Payload MessagePaginationRequest messagePaginationRequest,
                                     SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = Long.parseLong(headerAccessor.getSessionAttributes().get("AUTHENTICATED_MEMBER_ID").toString());
        ChatRoomMessageResponse response = chatService.loadPreviousMessages(chatRoomId, memberId, messagePaginationRequest);
        messagingTemplate.convertAndSendToUser(memberId.toString(), "/sub/chat.messages." + chatRoomId, response);
    }

    /**
     * * 아래로 스크롤 시 (이후 메시지 불러오기)
     * <p>
     * ! 프론트에서 보유한 가장 마지막 메시지ID로부터 아래로 20개 로딩
     * <p>
     * ! 특정 사용자에게 전송해야 하므로 convertAndSendToUser 메서드 활용
     * <p>
     * ! /user/{memberId}/sub/chat.messages.{chatRoomId}
     */
    @MessageMapping("/chat.loadNextMessages/{chatRoomId}")
    public void loadNextMessages(@DestinationVariable Long chatRoomId,
                                 @Payload MessagePaginationRequest messagePaginationRequest,
                                 SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = Long.parseLong(headerAccessor.getSessionAttributes().get("AUTHENTICATED_MEMBER_ID").toString());
        ChatRoomMessageResponse response = chatService.loadNextMessages(chatRoomId, memberId, messagePaginationRequest);
        messagingTemplate.convertAndSendToUser(memberId.toString(), "/sub/chat.messages." + chatRoomId, response);
    }
}
