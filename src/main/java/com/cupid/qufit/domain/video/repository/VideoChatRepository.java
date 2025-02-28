package com.cupid.qufit.domain.video.repository;


import com.cupid.qufit.entity.video.VideoChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

interface VideoChatMessageRepository extends MongoRepository<VideoChatMessage, Long> {
}
