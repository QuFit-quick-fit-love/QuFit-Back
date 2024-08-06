package com.cupid.qufit.domain.balancegame.repository;


import com.cupid.qufit.entity.video.BalanceGame;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BalanceGameRepository extends JpaRepository<BalanceGame, Long> {

    @Query(value = "SELECT * FROM balance_game ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<BalanceGame> findRandomBalanceGames();
}
