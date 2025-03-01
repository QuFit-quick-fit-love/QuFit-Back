package com.cupid.qufit.domain.video.service;


import com.cupid.qufit.domain.video.repository.VideoChatMessageRepository;
import com.cupid.qufit.domain.video.repository.VideoChatRoomRepository;
import com.cupid.qufit.domain.video.repository.VideoRoomRepository;
import com.cupid.qufit.entity.video.VideoChatMessage;
import com.cupid.qufit.entity.video.VideoChatRoom;
import com.cupid.qufit.entity.video.VideoRoom;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoChatService {

    private static final int MAX_PARTICIPANTS = 8;
//    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final VideoChatMessageRepository videoChatMessageRepository;
    private final VideoChatRoomRepository videoChatRoomRepository;


    public void sendMessage(VideoChatMessage message) {

        //  senderId,roomId,content는 담겨서옴, createdAt, id는 여기서 생성
        message.setCreatedAt(LocalDateTime.now());

        // Redis에 채팅 메시지 저장 (채팅방별 메시지 리스트)
        videoChatMessageRepository.save(message);

        // sub/chat/{roomId}으로 구독한 사용자에게 메시제 뿌리기 => 메세지 브로드캐스트
        //MongoDB에는 쌓이는 중임. 근데 websocket연결이 100% 되지가 않네. sub을 잘 못해.
        messagingTemplate.convertAndSend("/sub/chat/" + message.getRoomId(), message);
    }


        public void joinRoom(VideoChatMessage message) {
            String roomId = message.getRoomId().toString();
            // 채팅방 정보를 MongoDB에서 조회 (없으면 생성)
            VideoChatRoom room = videoChatRoomRepository.findById(roomId)
                                                    .orElseGet(() -> new VideoChatRoom(roomId));

            // 현재 참여자 수 체크
            if (room.getParticipantsCount() < MAX_PARTICIPANTS) {
                // 중복 참여 방지
                room.addParticipant(message.getSenderId());
                videoChatRoomRepository.save(room);

                // 세션에 채팅방 및 사용자 정보 저장
//                headerAccessor.getSessionAttributes().put("room_id", roomId);
//                headerAccessor.getSessionAttributes().put("username", message.getSenderId());

                // 참여 알림 메시지 브로드캐스트
                VideoChatMessage joinMsg = VideoChatMessage.builder()
                                                           .roomId(Long.parseLong(roomId))
                                                           .senderId(message.getSenderId())
                                                           .content(message.getSenderId() + "님이 채팅방에 참여했습니다.")
                                                           .createdAt(LocalDateTime.now())
                                                           .build();

//                messagingTemplate.convertAndSend("/sub/chat/" + roomId, joinMsg);
            } else {
                // 최대 인원 초과 시 에러 메시지 전송
                VideoChatMessage errorMsg = VideoChatMessage.builder()
                                                            .roomId(Long.parseLong(roomId))
                                                            .senderId(message.getSenderId())
                                                            .content("채팅방 정원이 초과되었습니다. (최대 8명)")
                                                            .createdAt(LocalDateTime.now())
                                                            .build();
                // 개별 사용자에게 에러 메시지 전송 (메시지 브로커 설정에 따라 활성화)
                // messagingTemplate.convertAndSendToUser(message.getSenderId(), "/queue/errors", errorMsg);
            }
        }

    public void leaveRoom(VideoChatMessage message) {
        String roomId = message.getRoomId().toString();
        // 채팅방 정보를 조회
        Optional<VideoChatRoom> optionalRoom = videoChatRoomRepository.findById(roomId);
        if (!optionalRoom.isPresent()) {
            // 채팅방이 없으면 더 이상 진행하지 않음
            return;
        }

        VideoChatRoom room = optionalRoom.get();

        // 참여자 목록에서 해당 사용자를 제거
        room.removeParticipant(message.getSenderId());

        // 남은 참여자가 없으면 채팅방 삭제, 그렇지 않으면 업데이트
        if (room.getParticipantsCount() <= 0) {
            videoChatRoomRepository.delete(room);
        } else {
            videoChatRoomRepository.save(room);
        }
        // 채팅방을 나갔음을 알리는 메시지 생성 및 브로드캐스트
//        VideoChatMessage leaveMsg = VideoChatMessage.builder()
//                                                    .roomId(Long.parseLong(roomId))
//                                                    .senderId(message.getSenderId())
//                                                    .content(message.getSenderId() + "님이 채팅방에서 나갔습니다.")
//                                                    .createdAt(LocalDateTime.now())
//                                                    .build();
//
//        messagingTemplate.convertAndSend("/sub/chat/" + roomId, leaveMsg);
//
    }
}
