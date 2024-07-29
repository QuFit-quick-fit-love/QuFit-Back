package com.cupid.qufit.domain.video.controller;

import com.cupid.qufit.domain.video.dto.VideoRoomRequest;
import com.cupid.qufit.domain.video.service.VideoRoomService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qufit/video")
@RequiredArgsConstructor
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

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

    /**
     * 방 업데이트
     */
    @PutMapping("/{videoRoomId}")
    public ResponseEntity<?> updateVideoRoom(@PathVariable Long videoRoomId, @RequestBody VideoRoomRequest videoRoomRequest) {
        return new ResponseEntity<>(videoRoomService.updateVideoRoom(videoRoomId, videoRoomRequest), HttpStatus.OK);
    }
}