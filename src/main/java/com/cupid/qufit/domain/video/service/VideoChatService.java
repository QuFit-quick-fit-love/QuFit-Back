package com.cupid.qufit.domain.video.service;


import com.cupid.qufit.entity.video.VideoChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VideoChatService {

    private static final int MAX_PARTICIPANTS = 8;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(VideoChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        // Redis에 채팅 메시지 저장 (채팅방별 메시지 리스트)
        String messageKey = "chat:room:" + message.getRoomId() + ":messages";
        redisTemplate.opsForList().rightPush(messageKey, message);

        // WebSocket을 통해 해당 채팅방에 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/" + message.getRoomId(), message);
    }

    public void joinRoom(VideoChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String roomParticipantsKey = "chat:room:" + message.getRoomId() + ":participants"; //redis에 저장될 key 값
        // 채팅방 참여 인원 증가 (Atomic 연산)
        Long count = redisTemplate.opsForValue().increment(roomParticipantsKey, 1);

        if (count != null && count <= MAX_PARTICIPANTS) {
            // 세션에 채팅방 및 사용자 정보 저장
            headerAccessor.getSessionAttributes().put("room_id", message.getRoomId());
            headerAccessor.getSessionAttributes().put("username", message.getSender());

            // 참여 알림 메시지 브로드캐스트
            VideoChatMessage joinMsg = VideoChatMessage.builder()
                                                       .roomId(message.getRoomId())
                                                       .sender("System")
                                                       .content(message.getSender() + "님이 채팅방에 참여했습니다.")
                                                       .timestamp(LocalDateTime.now())
                                                       .build();

            messagingTemplate.convertAndSend("/sub/chat/" + message.getRoomId(), joinMsg);
        } else {
            // 참여 인원이 8명을 초과한 경우, 카운터 복구 및 에러 메시지 전송
            redisTemplate.opsForValue().decrement(roomParticipantsKey);
            // 참여 알림 메시지 브로드캐스트
            VideoChatMessage errorMsg = VideoChatMessage.builder()
                                                       .roomId(message.getRoomId())
                                                       .sender("System")
                                                       .content("채팅방 정원이 초과되었습니다. (최대 8명)")
                                                       .timestamp(LocalDateTime.now())
                                                       .build();
            messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/errors", errorMsg);
        }
    }
}
