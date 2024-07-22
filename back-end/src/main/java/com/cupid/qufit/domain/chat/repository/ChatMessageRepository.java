package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime timestamp);

    long countByChatRoomIdAndTimestampAfterAndSenderIdNot(Long chatRoomId, LocalDateTime lastReadTime, Long currentMemberId);

    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampDesc(Long id);
}
