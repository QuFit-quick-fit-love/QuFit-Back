package com.cupid.qufit.domain.video.controller;


import com.cupid.qufit.domain.video.repository.VideoChatRoomRepository;
import com.cupid.qufit.domain.video.service.VideoChatService;
import com.cupid.qufit.entity.video.VideoChatMessage;
import com.cupid.qufit.entity.video.VideoChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class VideoChatController {

    private final VideoChatService videoChatService;
    private final VideoChatRoomRepository videoChatRoomRepository;

//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload VideoChatMessage message) {
//        videoChatService.sendMessage(message);
//    }

    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<?> getChatRoom(@PathVariable String roomId) {
         VideoChatRoom videoChatRoom = videoChatRoomRepository.findById(roomId).orElse(null);

         return ResponseEntity.ok(videoChatRoom);
    }

//    @MessageMapping("/chat.joinRoom")
    @PostMapping("/chat/joinRoom")
    public void joinRoom(@RequestBody VideoChatMessage message) {
        log.info("방 접속 들어옴");
        videoChatService.joinRoom(message);
    }

    @PostMapping("/chat/leaveRoom")
    public void leaveRoom(@RequestBody VideoChatMessage message) {
        videoChatService.leaveRoom(message);
    }
}
