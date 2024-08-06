package com.cupid.qufit.domain.balancegame.controller;

import com.cupid.qufit.domain.balancegame.dto.BalanceGameResult;
import com.cupid.qufit.domain.balancegame.dto.SaveChoice;
import com.cupid.qufit.domain.balancegame.service.BalanceGameService;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.entity.balancegame.BalanceGame;
import com.cupid.qufit.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/qufit/balance-game")
@RequiredArgsConstructor
@Tag(name = "밸런스 게임", description = "밸런스 게임 API")
public class BalanceGameController {

    private final BalanceGameService balanceGameService;

    /**
     * 밸런스 게임 뿌리기
     */
    @GetMapping("/random")
    @Operation(summary = "랜덤한 밸런스 게임 리스트 조회", description = "게임에서 사용할 밸런스 게임 리스트 조회 API. 랜덤한 밸런스게임 4개를 가져옵니다.")
    public ResponseEntity<List<BalanceGame>> getRandomBalanceGameList() {
        return ResponseEntity.ok(balanceGameService.getRandomBalanceGameList());
    }

    /**
     * 모든 밸런스 게임 리스트 조회
     *
     * @return AllBalanceGameList
     */
    @GetMapping
    @Operation(summary = "밸런스 게임 리스트 조회", description = "테스트용으로 만든 API")
    public ResponseEntity<List<BalanceGame>> getAllBalanceGameList() {
        return ResponseEntity.ok(balanceGameService.getAllBalanceGameList());
    }

    /**
     * 밸런스 게임 선택. 게임 중 유저가 밸런스게임 선택지를 제출했을때 DB에 저장하는 API
     *
     * @param saveChoiceRequest 유저가 선택한 밸런스 게임 선택지
     * @return 게임 선택지 저장 결과
     */
    @PostMapping
    @Operation(summary = "밸런스 게임 선택", description = "밸런스 게임에 대한 사용자의 선택을 저장하는 API")
    public ResponseEntity<SaveChoice.Response> selectBalanceGame(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                 @RequestBody SaveChoice.Request saveChoiceRequest) {
        return ResponseEntity.ok().body(balanceGameService.saveChoice(memberDetails.getId(), saveChoiceRequest));
    }

    @DeleteMapping("/{video_room_id}")
    public ResponseEntity<?> deleteAllBalanceGame(@PathVariable("video_room_id") Long videoRoomId) {
        balanceGameService.deleteAllChoice(videoRoomId);
        return ResponseEntity.ok().body(CommonResponse.builder().isSuccess(true).message("삭제 성공").build());
    }

    @GetMapping("/{video_room_id}")
    public ResponseEntity<List<BalanceGameResult>> getBalanceGameResultByVideoRoomId(
            @PathVariable("video_room_id") Long videoRoomId) {
        List<BalanceGameResult> balanceGameResultByVideoRoomId = balanceGameService.getBalanceGameResultByVideoRoomId(
                videoRoomId);

        return ResponseEntity.ok().body(balanceGameResultByVideoRoomId);
    }


}
