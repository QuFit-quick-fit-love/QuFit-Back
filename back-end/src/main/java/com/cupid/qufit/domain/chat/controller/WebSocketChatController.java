package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.ChatMessageTemp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketChatController {

    @MessageMapping("/chat/message") // ! -> 전체 경로 : /pub/chat/message
    @SendTo("/sub/channel") // ! 처리된 메시지를 발송할 구독 주제. (어느 목적지로 보낼 지 지정)
    public ChatMessageTemp sendMessage(@Payload ChatMessageTemp chatMessage) {
        System.out.println("메시지 수신 : " + chatMessage);
        log.info("메시지 수신 : {}", chatMessage);
        return chatMessage;
    }
}
