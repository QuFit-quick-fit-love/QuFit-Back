package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test/chat")
public class TestChatController {

    private final ChatService chatService;

    @GetMapping("/enter/{chatRoomId}")
    public ResponseEntity<?> getEnterRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @PathVariable("chatRoomId") Long chatRoomId) {
        Long memberId = memberDetails.getId();

        ChatRoomMessageResponse response = chatService.enterChatRoom(chatRoomId, memberId, 20);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
