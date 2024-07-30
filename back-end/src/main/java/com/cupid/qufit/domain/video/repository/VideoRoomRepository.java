package com.cupid.qufit.domain.video.repository;

import com.cupid.qufit.entity.video.VideoRoom;
import com.cupid.qufit.entity.video.VideoRoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {

    Page<VideoRoom> findByStatus(VideoRoomStatus status, Pageable pageable);
}
