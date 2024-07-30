package com.cupid.qufit.domain.video.controller;

import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.video.dto.VideoRoomRequest;
import com.cupid.qufit.domain.video.dto.VideoRoomResponse;
import com.cupid.qufit.domain.video.service.VideoRoomService;
import com.cupid.qufit.global.common.response.CommonResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "video", description = "미팅룸 관련 API")
public class VideoRoomController {

    private final VideoRoomService videoRoomService;

    /**
     * 미팅룸 생성
     */
    @PostMapping
    @Operation(summary = "새 비디오 방 생성", description = "제공된 세부 정보를 사용하여 새 비디오 방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = VideoRoomResponse.class)))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
    })
    public ResponseEntity<?> createVideoRoom(
            @Parameter(description = "생성할 비디오 방의 세부 정보", required = true) @RequestBody VideoRoomRequest videoRoomRequest) {
        return new ResponseEntity<>(videoRoomService.createVideoRoom(videoRoomRequest), HttpStatus.OK);
    }

    /**
     * 방 참가
     */
    @Operation(summary = "비디오 방 참여", description = "제공된 요청 데이터로 지정된 비디오 방에 참여합니다.")
    @PostMapping("{videoRoomId}/join")
    public ResponseEntity<?> joinVideoRoom(
            @Parameter(description = "참여할 비디오 방의 ID", required = true) @PathVariable Long videoRoomId,
            @Parameter(description = "참가자의 ID", required = true) @RequestParam Long participantId) {
        return new ResponseEntity<>(Map.of("token", videoRoomService.joinVideoRoom(videoRoomId, participantId)),
                HttpStatus.OK);
    }

    /**
     * 방 수정
     */
    @Operation(summary = "비디오 방 업데이트", description = "제공된 세부 정보로 지정된 비디오 방을 업데이트합니다.")
    @PutMapping("/{videoRoomId}")
    public ResponseEntity<?> updateVideoRoom(
            @Parameter(description = "업데이트할 비디오 방의 ID", required = true) @PathVariable Long videoRoomId,
            @Parameter(description = "비디오 방을 업데이트하기 위한 세부 정보", required = true) @RequestBody VideoRoomRequest videoRoomRequest) {
        return new ResponseEntity<>(videoRoomService.updateVideoRoom(videoRoomId, videoRoomRequest), HttpStatus.OK);
    }

    /**
     * 방 삭제
     */
    @Operation(summary = "비디오 방 삭제", description = "지정된 비디오 방을 삭제합니다.")
    @DeleteMapping("/{videoRoomId}")
    public ResponseEntity<?> deleteVideoRoom(
            @Parameter(description = "삭제할 비디오 방의 ID", required = true) @PathVariable Long videoRoomId) {
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
    @Operation(summary = "비디오 방 퇴장", description = "지정된 비디오 방에서 퇴장합니다.")
    @DeleteMapping("/{videoRoomId}/leave")
    public ResponseEntity<?> leaveVideoRoom(
            @Parameter(description = "퇴장할 비디오 방의 ID", required = true) @PathVariable Long videoRoomId,
            @Parameter(description = "비디오 방에서 퇴장할 참가자의 ID", required = true) @RequestParam Long participantId) {
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
    @Operation(summary = "비디오 방 상세 정보 조회", description = "지정된 비디오 방의 세부 정보를 조회합니다.")
    @GetMapping("/{videoRoomId}")
    public ResponseEntity<?> getVideoRoomDetail(@Parameter(description = "상세 정보를 조회할 비디오 방의 ID", required = true)
                                                @PathVariable Long videoRoomId) {
        return new ResponseEntity<>(videoRoomService.getVideoRoomDetail(videoRoomId), HttpStatus.OK);
    }

    /**
     * 방 리스트 조회 (최신순)
     */
    @Operation(summary = "비디오 방 목록 조회", description = "모든 비디오 방의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getVideoRoomList(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return new ResponseEntity<>(videoRoomService.getVideoRoomList(pageable), HttpStatus.OK);
    }
}