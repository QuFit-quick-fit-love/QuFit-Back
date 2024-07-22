package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime timestamp);

    long countByChatRoomIdAndTimestampAfterAndSenderIdNot(Long chatRoomId, LocalDateTime lastReadTime, Long currentMemberId);

    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampDesc(Long id);

    @Query(value = "{'chatRoomId': ?0, '_id': {$lt: ?1}}", sort = "{'timestamp': -1}")
    List<ChatMessage> findMessagesBeforeId(Long chatRoomId, String messageId, int limit);

    @Query(value = "{'chatRoomId': ?0, '_id': {$gte: ?1}}", sort = "{'timestamp': 1}")
    List<ChatMessage> findMessagesOnAndAfterId(Long chatRoomId, String messageId, int limit);

    @Query(value = "{'chatRoomId': ?0, 'timestamp': {$gt: ?1}}", count = true)
    int countUnreadMessages(Long chatRoomId, LocalDateTime timestamp);

    @Query(value = "{'chatRoomId': ?0}", sort = "{'timestamp': -1}")
    List<ChatMessage> findLatestMessages(Long chatRoomId, Pageable pageable);

}
