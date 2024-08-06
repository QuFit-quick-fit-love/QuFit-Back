package com.cupid.qufit.domain.balancegame.repository;

import com.cupid.qufit.entity.balancegame.BalanceGameChoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceGameChoiceRepository extends JpaRepository<BalanceGameChoice, Long> {

    void deleteAllByVideoRoomId(Long videoRoomId);
}

