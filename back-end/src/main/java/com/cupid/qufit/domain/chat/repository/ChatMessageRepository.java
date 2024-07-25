package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndTimestampAfter(Long chatRoomId, LocalDateTime timestamp);

    long countByChatRoomIdAndTimestampAfterAndSenderIdNot(Long chatRoomId, LocalDateTime lastReadTime, Long currentMemberId);


    Page<ChatMessage> findByChatRoomIdAndTimestampGreaterThanEqual(Long chatRoomId, LocalDateTime timestamp, Pageable pageable);

    @Query("{'chatRoomId' : ?0, '_id' : {$lt:  ?1}}") // ! chatRoomId가 주어진 값과 일치하고, '_id'가 messageId보다 작은 (이전의) 메시지 조회 -> pagesSize만큼.
    List<ChatMessage> findPreviousMessages(Long chatRoomId, String messageId, int pageSize);


}
