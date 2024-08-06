package com.cupid.qufit.domain.balancegame.controller;

import com.cupid.qufit.domain.balancegame.service.BalanceGameService;
import com.cupid.qufit.entity.video.BalanceGame;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qufit/balance-game")
@RequiredArgsConstructor
@Tag(name = "밸런스 게임", description = "밸런스 게임 API")
public class BalanceGameController {

    private final BalanceGameService balanceGameService;
    /*
     * 밸런스 게임 뿌리기
     */
    @GetMapping("/random")
    @Operation(summary = "랜덤한 밸런스 게임 리스트 조회", description = "게임에서 사용할 밸런스 게임 리스트 조회 API. 랜덤한 밸런스게임 4개를 가져옵니다.")
    public ResponseEntity<List<BalanceGame>> getRandomBalanceGameList() {
        return ResponseEntity.ok(balanceGameService.getRandomBalanceGameList());
    }

    @GetMapping("/")
    @Operation(summary = "밸런스 게임 리스트 조회", description = "테스트용으로 만든 API")
    public ResponseEntity<List<BalanceGame>> getAllBalanceGameList() {
        return ResponseEntity.ok(balanceGameService.getAllBalanceGameList());
    }

}
