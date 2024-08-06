package com.cupid.qufit.domain.balancegame.service;

import com.cupid.qufit.domain.balancegame.repository.BalanceGameRepository;
import com.cupid.qufit.entity.video.BalanceGame;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceGameServiceImpl implements BalanceGameService{

    private final BalanceGameRepository balanceGameRepository;

    @Override
    public List<BalanceGame> getRandomBalanceGameList() {
        return balanceGameRepository.findRandomBalanceGames();
    }

    @Override
    public List<BalanceGame> getAllBalanceGameList() {
        return balanceGameRepository.findAll();
    }
}
