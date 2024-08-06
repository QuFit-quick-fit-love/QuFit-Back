package com.cupid.qufit.entity.balancegame;

import com.cupid.qufit.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "balance_game_choice")
public class BalanceGameChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceGameChoiceId;

    private Long videoRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member", unique = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_game", unique = false)
    private BalanceGame balanceGame;

    @NotNull
    @Pattern(regexp = "^[0-2]$", message = "선택 입력값은 0, 1, 또는 2만 입력할 수 있습니다.")
    private Integer choiceNum;

}
