package com.cupid.qufit.domain.balancegame.service;

import com.cupid.qufit.entity.video.BalanceGame;
import java.util.List;

public interface BalanceGameService {

    List<BalanceGame> getRandomBalanceGameList();

    List<BalanceGame> getAllBalanceGameList();
}
