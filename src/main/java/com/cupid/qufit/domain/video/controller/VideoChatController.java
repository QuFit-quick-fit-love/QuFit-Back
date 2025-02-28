package com.cupid.qufit.domain.video.controller;


import com.cupid.qufit.domain.video.service.VideoChatService;
import com.cupid.qufit.entity.video.VideoChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VideoChatController {

    private final VideoChatService videoChatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload VideoChatMessage message) {
        videoChatService.sendMessage(message);
    }

    @MessageMapping("/chat.joinRoom")
    public void joinRoom(@Payload VideoChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        videoChatService.joinRoom(message, headerAccessor);
    }
}
