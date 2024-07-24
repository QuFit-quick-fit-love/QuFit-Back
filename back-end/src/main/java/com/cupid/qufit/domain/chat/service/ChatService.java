package com.cupid.qufit.domain.chat.service;

import com.cupid.qufit.domain.chat.dto.ChatMessageDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
import com.cupid.qufit.domain.chat.dto.ChatRoomRequest;
import com.cupid.qufit.domain.chat.repository.ChatMessageRepository;
import com.cupid.qufit.domain.chat.repository.ChatRoomMemberRepository;
import com.cupid.qufit.domain.chat.repository.ChatRoomRepository;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import com.cupid.qufit.entity.chat.ChatRoomMemberStatus;
import com.cupid.qufit.entity.chat.ChatRoomStatus;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.ChatException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * * 공통 사용 메서드 -> private : 에러 핸들링 처리 후 반환
     */
    // ! 채팅방 찾기
    private ChatRoom findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                                 .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    // ! 멤버 찾기
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                               .orElseThrow(() -> new ChatException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // ! 채팅방_멤버 찾기
    private ChatRoomMember findChatRoomMember(ChatRoom chatRoom, Member member) {
        return chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member)
                                       .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_MEMBER_NOT_FOUND));
    }

    // ! 특정 회원이 특정 채팅방의 회원인지 확인, 둘 다 일치하지 않으면 (주어진 memberId가 채팅방의 어느 멤버와도 일치하지 않으면) -> 에러
    private void validateChatRoomMember(ChatRoom chatRoom, Long memberId) {
        if (!chatRoom.getMember1().getId().equals(memberId) && !chatRoom.getMember2().getId().equals(memberId)) {
            throw new ChatException(ErrorCode.UNAUTHORIZED_CHAT_ACCESS);
        }
    }

    // ! ************* 아래부터 채팅 관련 핵심 메서드 ************* //

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
    public ChatMessage processChatMessage(Long chatRoomId, ChatMessage chatMessage) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        // ! 해당 멤버가 채팅방 멤버인지 검증
        validateChatRoomMember(chatRoom, chatMessage.getSenderId());
        // ! 메시지 보내기
        ChatMessage savedMessage = saveMessage(chatMessage);
        updateChatRoomList(chatRoom, savedMessage);
        return savedMessage;
    }

    private ChatMessage saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setCreatedAt(LocalDateTime.now());
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * * 채팅방 리스트 업데이트 처리
     *
     * @param chatRoom
     * @param chatMessage
     */
    private void updateChatRoomList(ChatRoom chatRoom, ChatMessage chatMessage) {
        Member sender = findMemberById(chatMessage.getSenderId());
        Member receiver = chatRoom.getOtherMember(sender);

        // ! 채팅방 업데이트
        chatRoom.setLastMessage(chatMessage.getContent());
        chatRoom.setLastMessageTime(chatMessage.getTimestamp());
        chatRoom.setLastMessageId(chatMessage.getId());
        chatRoomRepository.save(chatRoom);

        // ! 수신자의 unreadCount 증가
        ChatRoomMember receiverMember = findChatRoomMember(chatRoom, receiver);
        receiverMember.setUnreadCount(receiverMember.getUnreadCount() + 1);
        chatRoomMemberRepository.save(receiverMember);

        // ! 채팅방 업데이트 정보 전송
        sendChatRoomUpdate(sender, receiver, chatRoom);
    }

    /**
     * * 채팅방 업데이트 정보 보내기
     * <p>
     * ! 특정 유저에게만 메시지 전송 ! 발신자, 수신자 모두 반영
     */
    private void sendChatRoomUpdate(Member sender, Member receiver, ChatRoom chatRoom) {
        ChatRoomMember senderMember = findChatRoomMember(chatRoom, sender);
        ChatRoomMember receiverMember = findChatRoomMember(chatRoom, receiver);

        ChatRoomDTO senderDTO = ChatRoomDTO.from(chatRoom, senderMember, receiver);
        ChatRoomDTO receiverDTO = ChatRoomDTO.from(chatRoom, receiverMember, sender);

        // ! 발신자에게 업데이트 채팅방 정보 전송
        messagingTemplate.convertAndSend("/sub/chatroom-updates." + chatRoom.getId() + "." + sender.getId(), senderDTO);
        // ! 수신자에게 업데이트된 채팅방 정보 전송
        messagingTemplate.convertAndSend("/sub/chatroom-updates." + chatRoom.getId() + "." + receiver.getId(),
                                         receiverDTO);
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
        Member member1 = findMemberById(request.getMember1Id());
        Member member2 = findMemberById(request.getMember2Id());

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
     * * 특정 채팅방의 메시지 목록 조회
     *
     * @param chatRoomId      특정 채팅방 ID
     * @param currentMemberId 확인하고자 하는 회원 -> ! 발신자, 수신자 체크를 위해
     * @param pageable        페이징 정보
     * @return 채팅 메시지 정보
     */
    public ChatRoomMessageResponse getChatRoomMessages(Long chatRoomId, Long currentMemberId, Pageable pageable) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        validateChatRoomMember(chatRoom, currentMemberId);

        // ! step1. 채팅방_멤버 조회
        ChatRoomMember chatRoomMember = findChatRoomMember(chatRoom, findMemberById(currentMemberId));


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
        if (pageable.getPageNumber() == 0) {
            updateLastReadMessage(chatRoomMember, messagePage.getContent());
        }

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
        Member member = findMemberById(memberId);
        List<ChatRoomMember> activeChatRoomMembers = chatRoomMemberRepository.findByMemberAndStatus(member,
                                                                                                    ChatRoomMemberStatus.ACTIVE);

        return activeChatRoomMembers.stream().map(chatRoomMember -> createChatRoomDTO(chatRoomMember, member))
                                    .collect(Collectors.toList());
    }

    private ChatRoomDTO createChatRoomDTO(ChatRoomMember chatRoomMember, Member currentMember) {
        ChatRoom chatRoom = chatRoomMember.getChatRoom();
        Member otherMember = chatRoom.getOtherMember(currentMember);

        return ChatRoomDTO.from(chatRoom, chatRoomMember, otherMember);
    }

    /**
     * * 채팅방 입장 시 unreadCount 초기화, 최근 메시지 로딩
     * @param chatRoomId
     * @param memberId
     * @return
     */
    public ChatRoomMessageResponse enterChatRoom(Long chatRoomId, Long memberId, Pageable pageable) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        Member member = findMemberById(memberId);
        ChatRoomMember chatRoomMember = findChatRoomMember(chatRoom, member);

        // ! unreadCount 초기화
        chatRoomMember.setUnreadCount(0);
        // ! 마지막으로 읽은 메시지 이후의 메시지 조회
        String lastReadMessageId = chatRoomMember.getLastReadMessageId();
        Page<ChatMessage> messagePage;

        if (lastReadMessageId == null) {
            // 처음 입장 또는 모든 메시지를 읽은 후 재입장하는 경우, 최신 메시지부터 조회
            messagePage = chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);
        } else {
            // 읽지 않은 메시지가 있는 경우, 마지막으로 읽은 메시지 이후의 메시지 조회
            ChatMessage lastReadMessage = chatMessageRepository.findById(lastReadMessageId)
                                                               .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));
            messagePage = chatMessageRepository.findByChatRoomIdAndTimestampGreaterThanEqual(
                    chatRoomId, lastReadMessage.getTimestamp(), pageable);
        }

        // ! 메시지 목록 생성
        List<ChatMessageDTO> messages = messagePage.getContent().stream()
                                                   .map(ChatMessageDTO::from)
                                                   .collect(Collectors.toList());

        // ! 최신 메시지의 ID와 시간을 마지막으로 읽은 메시지로 업데이트
        if (!messages.isEmpty()) {
            ChatMessage latestMessage = messagePage.getContent().get(0); // ! 마지막 메시지
            chatRoomMember.setLastReadMessageId(latestMessage.getId());
            chatRoomMember.setLastReadTime(latestMessage.getTimestamp());
        }

        // ! ChatRoomMember 저장
        chatRoomMemberRepository.save(chatRoomMember);

        // ! 응답 생성 및 반환
        return ChatRoomMessageResponse.of(messages, 0, messagePage);
    }


}
