package com.cupid.qufit.domain.video.controller;

import com.cupid.qufit.domain.video.dto.VideoRoomRequest;
import com.cupid.qufit.domain.video.service.VideoRoomService;
import com.cupid.qufit.global.common.response.CommonResultResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
     * 방 수정
     */
    @PutMapping("/{videoRoomId}")
    public ResponseEntity<?> updateVideoRoom(@PathVariable Long videoRoomId,
                                             @RequestBody VideoRoomRequest videoRoomRequest) {
        return new ResponseEntity<>(videoRoomService.updateVideoRoom(videoRoomId, videoRoomRequest), HttpStatus.OK);
    }

    /**
     * 방 삭제
     */
    @DeleteMapping("/{videoRoomId}")
    public ResponseEntity<?> deleteVideoRoom(@PathVariable Long videoRoomId) {
        videoRoomService.deleteVideoRoom(videoRoomId);
        CommonResultResponse response = CommonResultResponse.builder()
                                                            .isSuccess(true)
                                                            .message("미팅룸이 성공적으로 삭제되었습니다.")
                                                            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 방 떠나기
     */
    @DeleteMapping("/{videoRoomId}/leave")
    public ResponseEntity<?> leaveVideoRoom(@PathVariable Long videoRoomId, @RequestParam Long participantId) {
        videoRoomService.leaveVideoRoom(videoRoomId, participantId);
        CommonResultResponse response = CommonResultResponse.builder()
                                                            .isSuccess(true)
                                                            .message("미팅룸에서 성공적으로 나왔습니다.")
                                                            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 방 상세 정보 조회
     */
    @GetMapping("/{videoRoomId}")
    public ResponseEntity<?> getVideoRoomDetail(@PathVariable Long videoRoomId) {
        return new ResponseEntity<>(videoRoomService.getVideoRoomDetail(videoRoomId), HttpStatus.OK);
    }
}