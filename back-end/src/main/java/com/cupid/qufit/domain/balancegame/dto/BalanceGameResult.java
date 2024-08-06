package com.cupid.qufit.domain.balancegame.dto;

import com.cupid.qufit.entity.balancegame.BalanceGameChoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BalanceGameResult {

    private Long videoRoomId;
    private Long balanceGameId;
    private Long balanceGameChoiceId;
    private Long memberId;
    private Integer choiceNum;
    private String choiceContent;

    public static BalanceGameResult toBalanceGameResult(BalanceGameChoice choice) {
        return BalanceGameResult.builder()
                                .videoRoomId(choice.getVideoRoom().getVideoRoomId())
                                .balanceGameId(choice.getBalanceGame().getBalanceGameId())
                                .balanceGameChoiceId(choice.getBalanceGameChoiceId())
                                .memberId(choice.getMember().getId())
                                .choiceNum(choice.getChoiceNum())
                                .choiceContent(choice.getChoiceContent())
                                .build();
    }

}
