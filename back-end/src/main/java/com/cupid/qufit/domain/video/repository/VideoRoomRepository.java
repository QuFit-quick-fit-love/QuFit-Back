package com.cupid.qufit.domain.video.repository;

import com.cupid.qufit.entity.video.VideoRoom;
import com.cupid.qufit.entity.video.VideoRoomStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {

    List<VideoRoom> findByStatusOrderByCreatedAtDesc(VideoRoomStatus status);
}
