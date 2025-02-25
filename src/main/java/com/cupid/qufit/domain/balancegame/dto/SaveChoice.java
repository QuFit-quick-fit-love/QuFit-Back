package com.cupid.qufit.domain.balancegame.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SaveChoice {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @NotNull(message = "balanceGameId는 필수값입니다.")
        private Long balanceGameId;

        private Long videoRoomId;

        @Builder.Default
        private Integer choiceNum = 0;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {

        private Long balanceGameChoiceId;
        private Long videoRoomId;
        private Long memberId;
        private Long balanceGameId;
        private Integer choiceNum;
        private String choiceContent;
    }


}
