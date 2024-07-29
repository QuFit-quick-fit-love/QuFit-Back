package com.cupid.qufit.domain.video.controller;

import com.cupid.qufit.domain.video.dto.VideoRoomRequest;
import com.cupid.qufit.domain.video.service.VideoRoomService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qufit/video")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;
//    /**
//     *
//     * 방 제목, 참가자 이름을 받아 방에 연결해준다.
//     * 응답 : token
//     */
//    @CrossOrigin(origins = "http://localhost:5080")
//    @PostMapping(value = "/token")
//    public ResponseEntity<Map<String, String>> createToken(@RequestBody Map<String, String> params) {
//        String roomName = params.get("roomName");
//        String participantName = params.get("participantName");
//
//        if (roomName == null || participantName == null) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("errorMessage", "roomName and participantName are required"));
//        }
//
//        AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
//        token.setName(participantName);
//        token.setIdentity(participantName);
//        token.addGrants(new RoomJoin(true), new RoomName(roomName));
//
//        return ResponseEntity.ok(Map.of("token", token.toJwt()));
//    }
//
//    /**
//     * 이벤트 수신용
//     */
//    @PostMapping(value = "/livekit/webhook", consumes = "application/webhook+json")
//    public ResponseEntity<String> receiveWebhook(@RequestHeader("Authorization") String authHeader, @RequestBody String body) {
//        WebhookReceiver webhookReceiver = new WebhookReceiver(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
//        try {
//            WebhookEvent event = webhookReceiver.receive(body, authHeader);
//            System.out.println("LiveKit Webhook: " + event.toString());
//        } catch (Exception e) {
//            System.err.println("Error validating webhook event: " + e.getMessage());
//        }
//        return ResponseEntity.ok("ok");
//    }

    /**
     * 미팅룸 생성
     */
    @PostMapping
    public ResponseEntity<?> createVideoRoom(@RequestBody VideoRoomRequest videoRoomRequest) {
        return new ResponseEntity<>(videoRoomService.createVideoRoom(videoRoomRequest), HttpStatus.OK);
    }

    /**
     * 방 참가
     */
    @PostMapping("{videoRoomId}/join")
    public ResponseEntity<?> joinVideoRoom(@PathVariable Long videoRoomId,
                                           @RequestBody VideoRoomRequest videoRoomRequest) {
        return new ResponseEntity<>(Map.of("token", videoRoomService.joinVideoRoom(videoRoomId, videoRoomRequest)),
                HttpStatus.OK);
    }
}