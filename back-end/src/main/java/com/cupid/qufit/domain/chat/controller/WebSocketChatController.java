package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.entity.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatController {

    private final ChatService chatService;

    /**
     * * 채팅 메시지 전송 처리
     * ! : 1:1만 고려
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

        System.out.println("메시지 수신 : " + chatMessage);
        log.info("메시지 수신 : {}", chatMessage);
        return chatService.saveMessage(chatMessage);
    }
}
