package com.cupid.qufit.domain.chat.service;

import com.cupid.qufit.domain.chat.dto.ChatMessageDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
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
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    /**
     * * 채팅 메시지 저장
     * <p>
     * * : 메시지 저장 시 timestamp 자동 설정
     * <p>
     * TODO : 메시지 저장 시 알림 기능 추가
     * TODO : Security를 통한 인증 로직 추가
     * TODO : JWT 토큰을 통함 메시지 송수신 -> 이후에 적용
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
     * * 채팅방 생성 or 기존 채팅방 반환
     * <p>
     * TODO : Member의 PK 대신 다른 식별 방식 적용 (본인은 JWT, 반대는 ? )
     *
     * @param request 채팅방 생성 요청 객체
     * @return 생성되거나 조회된 채팅방
     */
    public ChatRoom createChatRoom(ChatRoomRequest request) {
        // ! step1. 회원 찾음
        Member member1 = findMember(request.getMember1Id());
        Member member2 = findMember(request.getMember2Id());

        // ! 두 회원의 채팅방이 이미 존재하는지 확인
        // ! 존재하지 않으면 생성하고 반환
        return findExistingChatRoom(member1, member2).orElseGet(() -> createAndSaveNewChatRoom(member1, member2));

    }

    /**
     * * 두 회원 간의 기존 채팅방 찾기
     *
     * @param member1 첫번째 회원
     * @param member2 두번째 회원
     * @return 찾은 채팅방 (Optional)
     */
    private Optional<ChatRoom> findExistingChatRoom(Member member1, Member member2) {
        return chatRoomRepository.findByMember1AndMember2OrMember1AndMember2(member1, member2, member2, member1);
    }

    /**
     * * 새로운 채팅방 생성 & 저장
     *
     * @param member1
     * @param member2
     * @return 생성된 채팅방
     */
    private ChatRoom createAndSaveNewChatRoom(Member member1, Member member2) {
        ChatRoom chatRoom = ChatRoom.builder().member1(member1).member2(member2).createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now()).status(ChatRoomStatus.ACTIVE).build();

        chatRoom = chatRoomRepository.save(chatRoom);

        saveChatRoomMembers(chatRoom, member1, member2);

        return chatRoom;
    }

    /**
     * * 채팅방 멤버 저장
     *
     * @param chatRoom 채팅방
     * @param member1  첫번째 회원
     * @param member2  두번째 회원
     */
    private void saveChatRoomMembers(ChatRoom chatRoom, Member member1, Member member2) {
        ChatRoomMember chatRoomMember1 = createChatRoomMember(chatRoom, member1);
        ChatRoomMember chatRoomMember2 = createChatRoomMember(chatRoom, member2);
        chatRoomMemberRepository.saveAll(Arrays.asList(chatRoomMember1, chatRoomMember2));
    }

    /**
     * * 채팅방_멤버 객체 생성
     *
     * @param chatRoom 채팅방
     * @param member   회원
     * @return 생성된 채팅방 멤버 객체
     */
    private ChatRoomMember createChatRoomMember(ChatRoom chatRoom, Member member) {
        return ChatRoomMember.builder().chatRoom(chatRoom).member(member).status(ChatRoomMemberStatus.ACTIVE).build();
    }


    /**
     * * memberId로부터 해당 회원 있는지 확인
     * TODO : 이후에 예외처리 역시 정해놓은 예외 처리 방식 적용
     * TODO : JWT 토큰 통한 회원 확인 로직 변경
     *
     * @param memberId 회원ID
     * @return 회원 찾지 못한 경우 예외처리
     */
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
    }

    /**
     * * 특정 채팅방의 메시지 목록 조회
     *
     * @param chatRoomId      특정 채팅방 ID
     * @param currentMemberId 확인하고자 하는 회원 -> ! 발신자, 수신자 체크를 위해
     * @param pageable        페이징 정보
     * @return 채팅 메시지 정보
     */
    public ChatRoomMessageResponse getChatRoomMessages(Long chatRoomId, Long currentMemberId, Pageable pageable) {
        // ! step1. 채팅방 멤버 조회
        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId,
                                                                                             currentMemberId)
                                                                .orElseThrow(() -> new EntityNotFoundException(
                                                                        "채팅방 멤버 찾을 수 없음 "));

        // ! step2. 페이징 처리된 메시지 조회
        Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId,
                                                                                                   pageable);
        // ! step3. ChatMessageDTO로 변환
        List<ChatMessageDTO> messages = messagePage.getContent().stream().map(ChatMessageDTO::from)
                                                   .collect(Collectors.toList());

        // ! step4. 읽지 않은 메시지 수 계산
        // * 사용자가 마지막으로 읽은 시간 이후에 도착한 메시지 중 자신이 보내지 않은 메시지 수 계산(수신자 관점)
        long unreadCount = chatMessageRepository.countByChatRoomIdAndTimestampAfterAndSenderIdNot(chatRoomId,
                                                                                                  chatRoomMember.getLastReadTime(),
                                                                                                  currentMemberId);

        // ! step5. 첫번째 페이지일 때만 마지막으로 읽은 메시지 업데이트
        if(pageable.getPageNumber() == 0)
            updateLastReadMessage(chatRoomMember, messagePage.getContent());

        // ! step6. 종합해서 반환
        return ChatRoomMessageResponse.of(messages, unreadCount, messagePage);
    }

    /**
     * * 마지막으로 읽은 메시지 정보 업데이트
     *
     * @param chatRoomMember 채팅방 멤버 정보
     * @param messages       조회된 메시지 목록
     */
    private void updateLastReadMessage(ChatRoomMember chatRoomMember, List<ChatMessage> messages) {
        if (!messages.isEmpty()) {
            ChatMessage lastMessage = messages.get(0);
            chatRoomMember.setLastReadMessageId(lastMessage.getId());
            chatRoomMember.setLastReadTime(lastMessage.getTimestamp());

            chatRoomMemberRepository.save(chatRoomMember);
        }
    }
//

    /**
     * * 특정 채팅방 들어가게 되면 안 읽음 메시지 초기화
     */
    private void markMessagesAsRead(Long chatRoomId, Long memberId) {

    }

    /**
     * * 특정 채팅방의 새 메시지 조회
     *
     * @param chatRoomId   채팅방 ID
     * @param lastReadTime 마지막으로 읽은 시간
     * @return 새 채팅 메시지 리스트
     */
    public List<ChatMessage> getNewMessagesForChatRoom(Long chatRoomId, LocalDateTime lastReadTime) {
        return chatMessageRepository.findByChatRoomIdAndTimestampAfter(chatRoomId, lastReadTime);
    }

    /**
     * * 특정 회원의 채팅방 리스트 조회
     *
     * @param memberId 회원ID
     * @return
     */
    public List<ChatRoomDTO> getChatRooms(Long memberId) {
        Member member = findMember(memberId);
        List<ChatRoomMember> activeChatRoomMembers = chatRoomMemberRepository.findByMemberAndStatus(member, ChatRoomMemberStatus.ACTIVE);

        return activeChatRoomMembers.stream()
                                    .map(chatRoomMember -> createChatRoomDTO(chatRoomMember, member))
                                    .collect(Collectors.toList());
    }

    private ChatRoomDTO createChatRoomDTO(ChatRoomMember chatRoomMember, Member currentMember) {
        ChatRoom chatRoom = chatRoomMember.getChatRoom();
        Member otherMember = chatRoom.getOtherMember(currentMember);

        return ChatRoomDTO.from(chatRoom, chatRoomMember, otherMember);
    }
}
