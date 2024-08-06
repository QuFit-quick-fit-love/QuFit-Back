package com.cupid.qufit.domain.friend.controller;

import com.cupid.qufit.domain.friend.service.FriendService;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.global.common.response.CommonResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qufit/friend")
@RequiredArgsConstructor
@Tag(name = "friend", description = "친구 관련 API")
public class FriendController {

    private final FriendService friendService;

    /**
     * 친구 추가
     */
    @PostMapping
    @Operation(summary = "친구 추가", description = "일대일 미팅 중 친구 추가를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 추가 성공",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommonResultResponse.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")})
    public ResponseEntity<?> addFriend(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Parameter(description = "친구 아이디") @RequestParam Long friendId) {
        friendService.addFriend(memberDetails.getId(), friendId);
        CommonResultResponse response = CommonResultResponse.builder()
                                                            .isSuccess(true)
                                                            .message("친구 추가에 성공하였습니다.")
                                                            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 친구 삭제
     */
    @DeleteMapping("/{friendId}")
    @Operation(summary = "친구 삭제", description = "친구 삭제를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 삭제 성공",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommonResultResponse.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")})
    public ResponseEntity<?> deleteFriend(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Parameter(description = "친구 아이디") @PathVariable Long friendId) {
        friendService.deleteFriend(memberDetails.getId(), friendId);
        CommonResultResponse response = CommonResultResponse.builder()
                                                            .isSuccess(true)
                                                            .message("친구 삭제에 성공하였습니다.")
                                                            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
