package com.cupid.qufit.domain.chat.service;

import com.cupid.qufit.domain.chat.dto.ChatMessageDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomDTO;
import com.cupid.qufit.domain.chat.dto.ChatRoomMessageResponse;
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
import com.cupid.qufit.global.common.request.MessagePaginationRequest;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.ChatException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * @return 생성되거나 조회된 채팅방
     */
    public ChatRoom createChatRoom(Long memberId, Long otherMemberId) {
        // ! step1. 회원 찾음
        Member member1 = findMemberById(memberId);
        Member member2 = findMemberById(otherMemberId);

        // ! 두 회원의 채팅방이 이미 존재하는지 확인
        // ! 존재하지 않으면 생성하고 반환
        Optional<ChatRoom> existingChatRoom = findExistingChatRoom(member1, member2);
        if (existingChatRoom.isPresent()) {
            throw new ChatException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        return createAndSaveNewChatRoom(member1, member2);
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
        return ChatRoomMessageResponse.of(messages, unreadCount, null, null, null, null);
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
        Member otherMember = chatRoom.getOtherMember(currentMember); // ! 채팅 중인 반대편 사람

        return ChatRoomDTO.from(chatRoom, chatRoomMember, otherMember);
    }

    /**
     * * 채팅방 입장 시 unreadCount 초기화, 최근 메시지 로딩
     *
     * @param chatRoomId
     * @param memberId
     * @return
     */
    public ChatRoomMessageResponse enterChatRoom(Long chatRoomId, Long memberId, int pageSize) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        Member member = findMemberById(memberId);
        ChatRoomMember chatRoomMember = findChatRoomMember(chatRoom, member);

        List<ChatMessage> messages;
        String firstMessageId = chatMessageRepository.findTopByChatRoomIdOrderByTimestampAsc(chatRoomId)
                                                     .orElseThrow(
                                                             () -> new ChatException(ErrorCode.CHAT_MESSAGE_NOT_FOUND))
                                                     .getId();
        String lastMessageId = chatMessageRepository.findTopByChatRoomIdOrderByTimestampDesc(chatRoomId)
                                                    .orElseThrow(
                                                            () -> new ChatException(ErrorCode.CHAT_MESSAGE_NOT_FOUND))
                                                    .getId();

        Boolean isUnreadCountExceeded = chatRoomMember.getUnreadCount() > pageSize;


        if (chatRoomMember.getUnreadCount() > pageSize && chatRoomMember.getLastReadMessageId() != null) {
            // 안 읽은 메시지가 pageSize를 초과하는 경우
            ChatMessage lastReadMessage = chatMessageRepository.findById(chatRoomMember.getLastReadMessageId())
                                                               .orElseThrow(() -> new ChatException(
                                                                       ErrorCode.CHAT_MESSAGE_NOT_FOUND));

            messages = chatMessageRepository.findMessagesAfterTimestamp(chatRoomId, lastReadMessage.getTimestamp());
        } else {
            // 안 읽은 메시지가 pageSize 이하이거나 처음 입장하는 경우
            Page<ChatMessage> messagePage = chatMessageRepository.findLatestMessages(chatRoomId,
                                                                                     PageRequest.of(0, pageSize));
            messages = new ArrayList<>(messagePage.getContent());
            Collections.reverse(messages);
        }

        List<ChatMessageDTO> messageDTOs = messages.stream()
                                                   .map(ChatMessageDTO::from)
                                                   .collect(Collectors.toList());

        if (!messageDTOs.isEmpty()) {
            ChatMessageDTO latestMessage = messageDTOs.get(messageDTOs.size() - 1);
            chatRoomMember.setLastReadMessageId(latestMessage.getId());
            chatRoomMember.setLastReadTime(latestMessage.getTimestamp());
            chatRoomMember.setUnreadCount(0);
            chatRoomMemberRepository.save(chatRoomMember);
        }

        return ChatRoomMessageResponse.of(messageDTOs, null, null, firstMessageId, lastMessageId, isUnreadCountExceeded);
    }

    /**
     * * 위로 스크롤 시 이전 메시지 반환. (오름차순)
     *
     * @param chatRoomId
     * @param memberId
     * @param request
     * @return
     */
    public ChatRoomMessageResponse loadPreviousMessages(Long chatRoomId, Long memberId,
                                                        MessagePaginationRequest request) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        Member member = findMemberById(memberId);
//        ChatRoomMember chatRoomMember = findChatRoomMember(chatRoom, member);

        String messageId = request.getMessageId();
        Pageable pageable = PageRequest.of(0, request.getPageSize(), Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<ChatMessage> messagePage = chatMessageRepository.findPreviousMessages(chatRoomId, messageId, pageable);

        List<ChatMessageDTO> messageDTOs = messagePage.getContent().stream()
                                                      .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                                                      .map(ChatMessageDTO::from)
                                                      .collect(Collectors.toList());
        // ! 요청한 페이지 크기만큼 메시지가 조회됐다면 더 조회할 수 있다고 알리기 위해.
        boolean hasMore = messagePage.hasNext();
        return ChatRoomMessageResponse.of(messageDTOs, null, hasMore, null, null, null);
    }

    /**
     * * 아래로 스크롤 시 이후 메시지 반환 (오름차순)
     *
     * @param chatRoomId
     * @param memberId
     * @param request
     * @return
     */
    public ChatRoomMessageResponse loadNextMessages(Long chatRoomId, Long memberId,
                                                    MessagePaginationRequest request) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        Member member = findMemberById(memberId);
//        ChatRoomMember chatRoomMember = findChatRoomMember(chatRoom, member);

        String messageId = request.getMessageId();
        Pageable pageable = PageRequest.of(0, request.getPageSize(), Sort.by(Sort.Direction.ASC, "timestamp"));
        Page<ChatMessage> messagePage = chatMessageRepository.findNextMessages(chatRoomId, messageId, pageable);

        // ! 시간 순 정렬(이미 가져올 때부터 시간 순), DTO 변환을 한 번에 처리
        List<ChatMessageDTO> messageDTOs = messagePage.getContent().stream()
                                                      .map(ChatMessageDTO::from)
                                                      .collect(Collectors.toList());
        // ! 요청한 페이지 크기만큼 메시지가 조회됐다면 더 조회할 수 있다고 알리기 위해.
        boolean hasMore = messagePage.hasNext();

        return ChatRoomMessageResponse.of(messageDTOs, null, hasMore, null, null, null);
    }

    /**
     * * 채팅방 나가는 회원의 정보 갱신 -> 마지막으로 읽은 메시지ID, 메시지 내용
     *
     * @param chatRoomId
     * @param memberId
     */
    public void updateChatRoomMember(Long chatRoomId, Long memberId, ChatMessageDTO lastMessage) {
        ChatRoom chatRoom = findChatRoomById(chatRoomId);
        Member member = findMemberById(memberId);
        ChatRoomMember chatRoomMember = findChatRoomMember(chatRoom, member);

        // ! 클라이언트로부터 받은 마지막 메시지 정보 갱신
        if (lastMessage != null) {
            chatRoomMember.setLastReadMessageId(lastMessage.getId());
            chatRoomMember.setLastReadTime(lastMessage.getTimestamp());
        }

        chatRoomMember.setUnreadCount(0);

        chatRoomMemberRepository.save(chatRoomMember);
        chatRoomRepository.save(chatRoom);
    }
}
