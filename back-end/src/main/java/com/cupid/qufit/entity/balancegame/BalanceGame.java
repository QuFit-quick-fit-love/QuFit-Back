package com.cupid.qufit.entity.balancegame;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
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
@Entity
public class BalanceGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceGameId;

    @Builder.Default
    private String content = "";

    private String scenario1;

    private String scenario2;

    @OneToMany(mappedBy = "balanceGame")
    private List<BalanceGameChoice> choices;
}
