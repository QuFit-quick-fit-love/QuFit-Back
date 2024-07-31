package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime timestamp);

    long countByChatRoomIdAndTimestampAfterAndSenderIdNot(Long chatRoomId, LocalDateTime lastReadTime,
                                                          Long currentMemberId);

    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampDesc(Long chatRoomId);


    @Query("{'chatRoomId': ?0, '_id': {$lt: ?1}}")
    Page<ChatMessage> findPreviousMessages(Long chatRoomId, String messageId, Pageable pageable);

    @Query("{'chatRoomId': ?0, '_id': {$gt: ?1}}")
    Page<ChatMessage> findNextMessages(Long chatRoomId, String messageId, Pageable pageable);



    @Query("{'chatRoomId' : ?0, 'timestamp' : {$gte :  ?1}}")
    Page<ChatMessage> findMessagesAfterTimestamp(Long chatRoomId, LocalDateTime timestamp, Pageable pageable);

    @Query("{'chatRoomId' :  ?0}")
    Page<ChatMessage> findLatestMessages(Long chatRoomId, Pageable pageable);

}
