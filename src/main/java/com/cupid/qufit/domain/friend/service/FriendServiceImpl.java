package com.cupid.qufit.domain.friend.service;

import com.cupid.qufit.domain.chat.repository.ChatRoomRepository;
import com.cupid.qufit.domain.friend.dto.FriendDTO;
import com.cupid.qufit.domain.friend.repository.FriendRepository;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.entity.FriendRelationship;
import com.cupid.qufit.entity.FriendRelationshipStatus;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.FriendException;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FriendServiceImpl implements FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 친구 추가
     */
    @Override
    public void addFriend(Long memberId, Long friendId) {
        // ! 1. 현재 멤버와 친구의 정보를 가져옴
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Member friend = memberRepository.findById(friendId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // ! 2. 친구 관계 추가
        addFriendRelation(member, friend);
    }

    /**
     * 친구 삭제
     */
    @Override
    public void deleteFriend(Long memberId, Long friendId) {
        // ! 1. 현재 멤버와 친구의 정보를 가져옴
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Member friend = memberRepository.findById(friendId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // ! 2. 친구 관계 비활성화
        deleteFriendRelation(member, friend);
    }

    /**
     * 친구 리스트 조회 (닉네임 기준 오름차순)
     */
    @Override
    public Map<String, Object> getFriends(Long memberId, Pageable pageable) {
        // ! 1. 친구 관계에서 memberId의 친구 리스트를 ACTIVE 상태로 페이지네이션을 적용하여 조회
        Page<FriendRelationship> friendPage = friendRepository.findByMemberIdAndStatus(memberId,
                FriendRelationshipStatus.ACTIVE, pageable);

        // ! 2. 요청한 페이지가 최대 페이지 수보다 클 경우 에러처리
        if (pageable.getPageNumber() >= friendPage.getTotalPages()) {
            throw new FriendException(ErrorCode.INVALID_PAGE_REQUEST);
        }

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // ! 3. 친구 리스트 가공
        List<FriendDTO.Response> friendResponses = friendPage.stream()
                                                             .map(friendRelationship -> {
                                                                 Member friend = friendRelationship.getFriend();
                                                                 Optional<ChatRoom> chatRoom = chatRoomRepository.findByMember1AndMember2OrMember1AndMember2(member, friend, friend, member);
                                                                 return FriendDTO.Response.of(friend.getId(),
                                                                         chatRoom.map(ChatRoom::getId).orElse(null),
                                                                         friend.getNickname(),
                                                                         friend.getProfileImage());
                                                             })
                                                             .collect(Collectors.toList());

        // ! 3. 응답용 데이터 가공
        Map<String, Object> response = new HashMap<>();
        response.put("friendList", friendResponses);
        response.put("page", Map.of(
                "totalElements", friendPage.getTotalElements(),
                "totalPages", friendPage.getTotalPages(),
                "currentPage", friendPage.getNumber(),
                "pageSize", friendPage.getSize()
        ));
        return response;
    }

    /**
     * 친구 추가 | 활성화
     */
    private void addFriendRelation(Member member, Member friend) {
        // ! 1. 기존에 친구 관계가 없는지 확인
        FriendRelationship existingRelation = friendRepository.findByMemberAndFriend(member, friend).orElse(null);

        // ! 2. 기존에 친구 관계가 아니였다면 추가
        if (existingRelation == null) {
            FriendRelationship friendRelationship = FriendRelationship.builder()
                                                                      .member(member)
                                                                      .friend(friend)
                                                                      .status(FriendRelationshipStatus.ACTIVE)
                                                                      .createdAt(LocalDateTime.now())
                                                                      .updatedAt(LocalDateTime.now())
                                                                      .build();
            member.getFriends().add(friendRelationship);
            friendRepository.save(friendRelationship);
        } else { // ! 3. 이미 친구가 관계였을 경우
            if (existingRelation.getStatus().equals(FriendRelationshipStatus.ACTIVE)) { // ! 3.1 이미 활성화 상태
                throw new FriendException(ErrorCode.FRIEND_ALREADY_EXISTS);
            } else { // ! 3.2 비활성화 상태라면 활성화
                existingRelation.setStatus(FriendRelationshipStatus.ACTIVE);
                existingRelation.setUpdatedAt(LocalDateTime.now());
                friendRepository.save(existingRelation);
            }
        }

    }

    /**
     * 친구 비활성화
     */
    private void deleteFriendRelation(Member member, Member friend) {
        // ! 1. 친구 관계가 존재하는지 검사
        FriendRelationship friendRelationship = friendRepository.findByMemberAndFriend(member, friend)
                                                                .orElseThrow(() -> new FriendException(
                                                                        ErrorCode.FRIEND_NOT_FOUND));

        // ! 2. 활성화 상태라면 비활성화 상태로 변경
        if (friendRelationship.getStatus().equals(FriendRelationshipStatus.ACTIVE)) {
            friendRelationship.setStatus(FriendRelationshipStatus.INACTIVE);
            friendRelationship.setUpdatedAt(LocalDateTime.now());
            friendRepository.save(friendRelationship);
        } else { // ! 2. 이미 비활성화 상태일 경우
            throw new FriendException(ErrorCode.FRIEND_ALREADY_INACTIVE);
        }
    }
}
