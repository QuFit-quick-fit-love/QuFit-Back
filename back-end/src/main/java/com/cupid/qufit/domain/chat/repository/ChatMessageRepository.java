package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);

    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime timestamp);
}
