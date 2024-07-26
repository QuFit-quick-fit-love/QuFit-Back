package com.cupid.qufit.domain.chat.controller;

import com.cupid.qufit.domain.chat.dto.ChatMessageDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.dto.ChatRoomRequest;
import com.cupid.qufit.domain.chat.service.ChatService;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qufit/chat")
@Tag(name = "Chat", description = "채팅 관련 REST API")
public class ChatController {

    private final ChatService chatService;

    /**
     * * 채팅방 생성
     */

    @PostMapping("/rooms")
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공",
                         content = @Content(schema = @Schema(implementation = ChatRoom.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저 오류"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoomRequest request) {
        ChatRoom chatRoom = chatService.createChatRoom(request);
        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }

    /**
     * * 유저의 채팅방 목록 조회
     * ! 초기 호출만 진행
     * <p>
     * TODO : 사용자 정보 지금은 member PK로 사용 이후에 변경 필요
     */
    @GetMapping("/rooms/{memberId}")
    @Operation(summary = "유저의 채팅방 목록 조회", description = "특정 사용자의 채팅방 목록을 조회. 초기 호출에만 REST API로 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatRoomDTO.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> getChatRooms(@PathVariable("memberId") Long memberId) {
        // TODO : 구현 필요
        List<ChatRoomDTO> chatRooms = chatService.getChatRooms(memberId);
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    /**
     * * 특정 채팅방의 메시지 목록 조회
     * <p>
     * @param  chatRoomId 채팅방 ID
     * @param memberId 조회하는 회원ID
     * @param pageable 페이징 정보
     * @return 채팅 메시지 목록 및 관련 정보
     */
    @GetMapping("/rooms/{chatRoomId}/messages/{memberId}")
    public ResponseEntity<?> getChatMessages(@PathVariable("chatRoomId") Long chatRoomId,
                                             @PathVariable("memberId") Long memberId,
                                             @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.ASC) Pageable pageable) {
        ChatRoomMessageResponse result = chatService.getChatRoomMessages(chatRoomId, memberId, pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * * 메시지 읽음 상태 관리
     */
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId, @RequestParam Long userId) {
        // TODO: 구현 필요
        return null;
    }

    @GetMapping("/rooms/{roomId}/unread-count")
    public ResponseEntity<Integer> getUnreadMessageCount(@PathVariable Long roomId, @RequestParam Long userId) {
        // TODO: 구현 필요
        return null;
    }

    /**
     * * 채팅방 나가기
     */
    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId, @RequestParam Long userId) {
        // TODO: 구현 필요
        return null;
    }


}
