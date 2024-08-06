package com.cupid.qufit.domain.balancegame.service;

import com.cupid.qufit.domain.balancegame.dto.SaveChoice;
import com.cupid.qufit.domain.balancegame.dto.SaveChoice.Request;
import com.cupid.qufit.entity.balancegame.BalanceGame;
import java.util.List;

public interface BalanceGameService {

    List<BalanceGame> getRandomBalanceGameList();

    List<BalanceGame> getAllBalanceGameList();

    SaveChoice.Response saveChoice(Long memberId, Request saveChoiceRequest);

    void deleteAllChoice(Long videoRoomId);
}
