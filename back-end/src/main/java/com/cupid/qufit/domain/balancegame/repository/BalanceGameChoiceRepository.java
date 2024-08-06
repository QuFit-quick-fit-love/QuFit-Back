package com.cupid.qufit.domain.balancegame.repository;

import com.cupid.qufit.entity.balancegame.BalanceGameChoice;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BalanceGameChoiceRepository extends JpaRepository<BalanceGameChoice, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM balance_game_choice b WHERE b.videoRoom.videoRoomId = :videoRoomId")
    void deleteByVideoRoom(@Param("videoRoomId") Long videoRoomId);

    @Query("SELECT b FROM balance_game_choice b WHERE b.videoRoom.videoRoomId = :videoRoomId order by b.member.id")
    List<BalanceGameChoice> findAllByVideoRoomIdOrderByMember(@Param("videoRoomId") Long videoRoomId);
}

