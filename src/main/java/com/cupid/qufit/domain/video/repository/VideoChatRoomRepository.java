package com.cupid.qufit.domain.video.repository;

import com.cupid.qufit.entity.video.VideoChatMessage;
import com.cupid.qufit.entity.video.VideoChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoChatRoomRepository extends MongoRepository<VideoChatRoom, String> {

}
