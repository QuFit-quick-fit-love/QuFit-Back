package com.cupid.qufit.domain.balancegame.repository;

import com.cupid.qufit.entity.balancegame.BalanceGameChoice;
import com.cupid.qufit.entity.video.VideoRoom;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BalanceGameChoiceRepository extends JpaRepository<BalanceGameChoice, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM balance_game_choice b WHERE b.videoRoom = :videoRoom")
    void deleteByVideoRoom(@Param("videoRoom") VideoRoom videoRoom);

    @Query("SELECT b FROM balance_game_choice b WHERE b.videoRoom.videoRoomId = :videoRoomId")
    List<BalanceGameChoice> findAllByVideoRoomIdOrderByMember(@Param("videoRoomId") Long videoRoomId);
}

