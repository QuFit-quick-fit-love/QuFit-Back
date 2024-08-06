package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatMessageDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.Path;

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

    @PostMapping("/leave/{chatRoomId}")
    public ResponseEntity<?> leaveRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                       @RequestBody ChatMessageDTO lastMessage,
                                       @PathVariable("chatRoomId") Long chatRoomId) {
        Long memberId = memberDetails.getId();
        chatService.updateChatRoomMember(chatRoomId, memberId, lastMessage);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/room/{chatRoomId}")
    public ResponseEntity<?> removeRoom(@AuthenticationPrincipal MemberDetails memberDetails,
                                        @PathVariable("chatRoomId") Long chatRoomId) {
        Long memberId = memberDetails.getId();
        List<ChatRoomDTO> chatRoomDTOS = chatService.removeChatRoom(chatRoomId, memberId);

        return new ResponseEntity<>(chatRoomDTOS, HttpStatus.OK);
    }
}
