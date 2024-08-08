package com.cupid.qufit.domain.balancegame.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class BalanceGameDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @Builder
    public static class Request {
        private Boolean isRoomStart;
        private Boolean isGameStart;
        private Boolean isChoiceStart;
        private Boolean getResult;
        private Long balanceGameId;
        private Long videoRoomId;
        private Integer answer;
    }
}