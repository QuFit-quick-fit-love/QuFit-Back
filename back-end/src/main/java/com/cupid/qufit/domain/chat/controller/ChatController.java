package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatRoomRequest;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * * 특정 채팅방의 메시지 목록 조회
     * <p>
     * TODO : 페이징 처리 추가
     *
     * @return 채팅 메시지 목록
     * @Param chatRoomId 채팅방 ID
     */

    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable("chatRoomId") Long chatRoomId) {
        List<ChatMessage> result = chatService.getMessagesForChatRoom(chatRoomId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * * 채팅방 생성
     */

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoomRequest request) {
        ChatRoom chatRoom = chatService.createChatRoom(request);
        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }
}
