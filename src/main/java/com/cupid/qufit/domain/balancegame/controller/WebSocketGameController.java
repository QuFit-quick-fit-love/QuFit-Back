package com.cupid.qufit.domain.balancegame.controller;

import com.cupid.qufit.domain.balancegame.dto.BalanceGameDTO;
import com.cupid.qufit.domain.balancegame.dto.BalanceGameDTO.Request;
import com.cupid.qufit.domain.balancegame.dto.BalanceGameResult;
import com.cupid.qufit.domain.balancegame.dto.SaveChoice;
import com.cupid.qufit.domain.balancegame.service.BalanceGameService;
import com.cupid.qufit.domain.friend.service.FriendService;
import com.cupid.qufit.domain.friend.service.FriendServiceImpl;
import com.cupid.qufit.entity.balancegame.BalanceGame;
import com.cupid.qufit.global.common.response.CommonResultResponse;
import com.cupid.qufit.global.common.response.GameResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GameWebSocket", description = "밸런스 게임 관련 웹소켓 통신 API")
public class WebSocketGameController {

    private final BalanceGameService balanceGameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendService friendService;

    /**
     * * 모든 게임 관련 웹소켓 메시지를 받는 중앙 허브 ! /pub/game/{videoRoomId} 엔드포인트로 들어오는 모든 메시지 처리
     *
     * @param videoRoomId
     * @param request
     * @param headerAccessor
     */
    @MessageMapping("/game/{videoRoomId}")
    public void handleGameMessage(@DestinationVariable Long videoRoomId,
                                  @Payload BalanceGameDTO.Request request,
                                  SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = Long.parseLong(headerAccessor.getSessionAttributes().get("AUTHENTICATED_MEMBER_ID").toString());

        if (request.getIsRoomStart() != null && request.getIsRoomStart()) {
            handleRoomStart(videoRoomId);
        } else if (request.getIsGameStart() != null && request.getIsGameStart()) {
            handleGameStart(videoRoomId);
        } else if (request.getIsChoiceStart() != null && request.getIsChoiceStart()) {
            handleChoiceStart(videoRoomId);
        } else if (request.getAnswer() != null) {
            handleChoice(videoRoomId, request, memberId);
        } else if (request.getGetResult() != null && request.getGetResult()) {
            handleGetResult(videoRoomId);
        } else if (request.getIsGameEnd() != null && request.getIsGameEnd()) {
            handleGameEnd(videoRoomId);
        } else if (request.getIsFriend() != null) {
            handleFriendConfirmation(videoRoomId, request, memberId);
        } else if (request.getMemberA() != null && request.getMemberB() != null) {
            handleFriendAddition(videoRoomId, request);
        } else {
            log.warn("인식되지 않은 요청 : videoRoomId: {}, request: {}", videoRoomId, request);
        }
    }


    /**
     * * 방 시작 요청 처리
     *
     * @param videoRoomId
     */
    private void handleRoomStart(Long videoRoomId) {
        log.info("방 시작 요청 처리 videoRoomId: {}", videoRoomId);
        GameResponse<CommonResultResponse> response = GameResponse.<CommonResultResponse>builder()
                                                                  .result(CommonResultResponse.builder()
                                                                                              .isSuccess(true)
                                                                                              .build())
                                                                  .message("미팅룸 시작을 성공했습니다.")
                                                                  .build();
        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("미팅룸 시작 메시지 전송 완료 videoRoomId: {}", videoRoomId);
    }

    /**
     * * 게임 시작 요청 처리
     *
     * @param videoRoomId
     */
    private void handleGameStart(Long videoRoomId) {
        log.info("게임 시작 요청 처리 videoRoomId: {}", videoRoomId);
        List<BalanceGame> games = balanceGameService.getRandomBalanceGameList();
        GameResponse<List<BalanceGame>> response = GameResponse.<List<BalanceGame>>builder()
                                                               .result(games)
                                                               .message("게임 시작을 성공했습니다.")
                                                               .build();
        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("게임 시작 메시지 전송 완료 videoRoomId: {}, 게임 수: {}", videoRoomId, games.size());
    }

    /**
     * * 선택 시작 요청 처리
     *
     * @param videoRoomId
     */
    private void handleChoiceStart(Long videoRoomId) {
        log.info("선택 시작 요청 처리 videoRoomId: {}", videoRoomId);
        GameResponse<Map<String, Boolean>> response = GameResponse.<Map<String, Boolean>>builder()
                                                                  .result(Map.of("isChoiceStart", true))
                                                                  .message("선택지 선택을 시작했습니다.")
                                                                  .build();
        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("선택 시작 메시지 전송 완료 videoRoomId: {}", videoRoomId);
    }

    /**
     * * 사용자의 선택 처리
     *
     * @param videoRoomId
     * @param request
     * @param memberId
     */
    private void handleChoice(Long videoRoomId, BalanceGameDTO.Request request, Long memberId) {
        log.info("사용자 선택 처리 videoRoomId: {}, memberId: {}, balanceGameId: {}, answer: {}",
                 videoRoomId, memberId, request.getBalanceGameId(), request.getAnswer());
        SaveChoice.Request saveChoiceRequest = new SaveChoice.Request(
                request.getBalanceGameId(), videoRoomId, request.getAnswer());
        SaveChoice.Response choiceResponse = balanceGameService.saveChoice(memberId, saveChoiceRequest);
        GameResponse<SaveChoice.Response> response = GameResponse.<SaveChoice.Response>builder()
                                                                 .result(choiceResponse)
                                                                 .message("선택을 완료했습니다.")
                                                                 .build();
        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("사용자 선택 저장 및 응답 전송 완료 videoRoomId: {}, memberId: {}", videoRoomId, memberId);
    }

    /**
     * * 게임 결과 조회 요청 처리
     *
     * @param videoRoomId
     */
    private void handleGetResult(Long videoRoomId) {
        log.info("게임 결과 조회 요청 처리 videoRoomId: {}", videoRoomId);
        List<BalanceGameResult> results = balanceGameService.getBalanceGameResultByVideoRoomId(videoRoomId);
        GameResponse<List<BalanceGameResult>> response = GameResponse.<List<BalanceGameResult>>builder()
                                                                     .result(results)
                                                                     .message("게임 결과를 조회했습니다.")
                                                                     .build();
        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("게임 결과 전송 완료 videoRoomId: {}, 결과 수: {}", videoRoomId, results.size());
    }

    /**
     * * 게임 종료 메시지
     *
     * @param videoRoomId
     */
    private void handleGameEnd(Long videoRoomId) {
        log.info("게임 종료 요청 처리 videoRoomId: {}", videoRoomId);
        GameResponse<CommonResultResponse> response = GameResponse.<CommonResultResponse>builder()
                                                                  .result(CommonResultResponse.builder()
                                                                                              .isSuccess(true)
                                                                                              .build())
                                                                  .message("게임이 종료됐습니다.")
                                                                  .build();
        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("게임 종료 메시지 전송 완료 videoRoomId: {}", videoRoomId);
    }

    /**
     * 친구 확인 요청 처리
     *
     * @param videoRoomId
     * @param request
     * @param memberId
     */
    private void handleFriendConfirmation(Long videoRoomId, BalanceGameDTO.Request request, Long memberId) {
        log.info("친구 확인 요청 처리 videoRoomId: {}, memberId: {}", videoRoomId, memberId);

        boolean isFriendAccepted = request.getIsFriend(); // true: 승락, false: 거절
        String message = isFriendAccepted ? "상대방이 친구를 수락했습니다." : "상대방이 친구를 거절했습니다.";

        GameResponse<Map<String, Boolean>> response = GameResponse.<Map<String, Boolean>>builder()
                                                                  .result(Map.of("isSuccess", isFriendAccepted))
                                                                  .message(message)
                                                                  .build();

        messagingTemplate.convertAndSendToUser(memberId.toString(), "/sub/game", response);
        log.info("친구 확인 메시지 전송 완료 videoRoomId: {}, memberId: {}", videoRoomId, memberId);
    }

    /**
     * 친구 추가 요청 처리
     * @param videoRoomId
     * @param request
     */
    private void handleFriendAddition(Long videoRoomId, BalanceGameDTO.Request request) {
        log.info("친구 추가 요청 처리 videoRoomId: {}, memberA: {}, memberB: {}",
                 videoRoomId, request.getMemberA(), request.getMemberB());

        // ! 친구 추가
        friendService.addFriend(request.getMemberA(), request.getMemberB());
        friendService.addFriend(request.getMemberB(), request.getMemberA());

        GameResponse<Map<String, Boolean>> response = GameResponse.<Map<String, Boolean>>builder()
                                                                  .result(Map.of("isSuccess", true))
                                                                  .message("친구가 추가되었습니다.")
                                                                  .build();

        messagingTemplate.convertAndSend("/sub/game/" + videoRoomId, response);
        log.info("친구 추가 메시지 전송 완료 videoRoomId: {}, memberA: {}, memberB: {}", videoRoomId, request.getMemberA(), request.getMemberB());
    }
}

