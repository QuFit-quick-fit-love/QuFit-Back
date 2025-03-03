package com.cupid.qufit.domain.video.service;

import com.cupid.qufit.domain.video.repository.VideoChatMessageRepository;
import com.cupid.qufit.domain.video.repository.VideoChatRoomRepository;
import com.cupid.qufit.entity.video.VideoChatMessage;
import com.cupid.qufit.entity.video.VideoChatRoom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class VideoChatService {

    private static final int MAX_PARTICIPANTS = 8;
    // 채팅방별로 Lock을 관리하기 위한 맵
    private static final ConcurrentHashMap<String, ReentrantLock> roomLocks = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;
    private final VideoChatMessageRepository videoChatMessageRepository;
    private final VideoChatRoomRepository videoChatRoomRepository;

    public void sendMessage(VideoChatMessage message) {
        message.setCreatedAt(LocalDateTime.now());
        videoChatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/sub/chat/" + message.getRoomId(), message);
    }

    public void joinRoom(VideoChatMessage message) {
        String roomId = message.getRoomId().toString();
        ReentrantLock lock = roomLocks.computeIfAbsent(roomId, key -> new ReentrantLock());

        //Lock 획득과정
        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                log.info("lock 획득에 실패했습니다.");
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            //예외처리
        }

        try {
            //임계구역
            VideoChatRoom room = videoChatRoomRepository.findById(roomId)
                                                        .orElseGet(() -> new VideoChatRoom(roomId));

            if (room.getParticipantsCount() < MAX_PARTICIPANTS) {
                room.addParticipant(message.getSenderId());
                videoChatRoomRepository.save(room);

                VideoChatMessage joinMsg = VideoChatMessage.builder()
                                                           .roomId(Long.parseLong(roomId))
                                                           .senderId(message.getSenderId())
                                                           .content(message.getSenderId() + "님이 채팅방에 참여했습니다.")
                                                           .createdAt(LocalDateTime.now())
                                                           .build();
                // messagingTemplate.convertAndSend("/sub/chat/" + roomId, joinMsg);
            } else {
                VideoChatMessage errorMsg = VideoChatMessage.builder()
                                                            .roomId(Long.parseLong(roomId))
                                                            .senderId(message.getSenderId())
                                                            .content("채팅방 정원이 초과되었습니다. (최대 8명)")
                                                            .createdAt(LocalDateTime.now())
                                                            .build();
                // messagingTemplate.convertAndSendToUser(message.getSenderId(), "/queue/errors", errorMsg);
            }
        } finally {
            lock.unlock();
        }
    }


    public void leaveRoom(VideoChatMessage message) {
        String roomId = message.getRoomId().toString();
        ReentrantLock lock = roomLocks.computeIfAbsent(roomId, key -> new ReentrantLock());

        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            Optional<VideoChatRoom> optionalRoom = videoChatRoomRepository.findById(roomId);

            if (!optionalRoom.isPresent()) {
                return;
            }

            VideoChatRoom room = optionalRoom.get();
            room.removeParticipant(message.getSenderId());
            if (room.getParticipantsCount() <= 0) {
                videoChatRoomRepository.delete(room);
            } else {
                videoChatRoomRepository.save(room);
            }

        } finally {
            lock.unlock();
        }

    }
}
