package com.cupid.qufit.domain.chat.service;

import com.cupid.qufit.domain.chat.dto.ChatRoomRequest;
import com.cupid.qufit.domain.chat.repository.ChatMessageRepository;
import com.cupid.qufit.domain.chat.repository.ChatRoomMemberRepository;
import com.cupid.qufit.domain.chat.repository.ChatRoomRepository;
import com.cupid.qufit.domain.member.repository.MemberRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import com.cupid.qufit.entity.chat.ChatRoomMemberStatus;
import com.cupid.qufit.entity.chat.ChatRoomStatus;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    /**
     * * 채팅 메시지 저장
     * <p>
     * TODO : 메시지 저장 시 알림 기능 추가
     * * : 메시지 저장 시 timestamp 자동 설정
     * ? : 이후에 security를 통해서 설정 추가
     * ? : JWT 토큰을 통해서 메시지 송수신 진행 검토
     *
     * @param : chatMessage 저장할 채팅 메시지
     * @return 저장된 채팅 메시지
     */
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setCreatedAt(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * * 채팅방 생성
     */
    public ChatRoom createChatRoom(ChatRoomRequest request) {
        // ! step1. 회원 찾음
        Member member1 = memberRepository.findById(request.getMember1Id())
                                         .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        Member member2 = memberRepository.findById(request.getMember2Id())
                                         .orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));

        // ! step2. 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                                    .member1(member1)
                                    .member2(member2)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .status(ChatRoomStatus.ACTIVE)
                                    .build();
        // ! 채팅방 저장
        chatRoomRepository.save(chatRoom);
        // ! step3. 채팅방에 참여한 사람 저장
        ChatRoomMember chatRoomMember1 = ChatRoomMember.builder()
                                                       .chatRoom(chatRoom)
                                                       .member(member1)
                                                       .status(ChatRoomMemberStatus.ACTIVE)
                                                       .build();

        ChatRoomMember chatRoomMember2 = ChatRoomMember.builder()
                                                       .chatRoom(chatRoom)
                                                       .member(member2)
                                                       .status(ChatRoomMemberStatus.ACTIVE)
                                                       .build();
        // ! 현재 채팅방 참여 정보 저장.
        chatRoomMemberRepository.saveAll(Arrays.asList(chatRoomMember1, chatRoomMember2));
        return chatRoom;
    }

    public List<ChatMessage> getMessagesForChatRoom(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }

    public List<ChatMessage> getNewMessagesForChatRoom(Long chatRoomId, LocalDateTime lastReadTime) {
        return chatMessageRepository.findByChatRoomIdAndTimestampAfter(chatRoomId, lastReadTime);
    }

}
