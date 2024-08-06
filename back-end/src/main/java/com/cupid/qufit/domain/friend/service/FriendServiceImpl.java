package com.cupid.qufit.domain.friend.service;

import com.cupid.qufit.domain.friend.repository.FriendRepository;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.entity.FriendRelationship;
import com.cupid.qufit.entity.FriendRelationshipStatus;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.FriendException;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FriendServiceImpl implements FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    @Override
    public void addFriend(Long memberId, Long friendId) {
        // ! 1. 현재 멤버와 친구의 정보를 가져옴
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Member friend = memberRepository.findById(friendId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // ! 2. 친구 관계 추가
        /*
        실제 동작은 각 사용자가 addFriend 를 호출하기 때문에
        addFriendRelation(friend, member); 필요 X
        지금은 테스트를 위해서 추가한 것
         */
        addFriendRelation(member, friend);
        addFriendRelation(friend, member);
    }

    @Override
    public void deleteFriend(Long memberId, Long friendId) {
        // ! 1. 현재 멤버와 친구의 정보를 가져옴
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Member friend = memberRepository.findById(friendId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // ! 2. 친구 관계 삭제
        deleteFriendRelation(member, friend);
    }

    private void addFriendRelation(Member member, Member friend) {
        // 기존에 친구 관계가 없는지 확인
        if (friendRepository.existsByMemberAndFriend(member, friend)) {
            throw new FriendException(ErrorCode.FRIEND_ALREADY_EXISTS);
        }

        FriendRelationship friendRelationship = FriendRelationship.builder()
                                                                  .member(member)
                                                                  .friend(friend)
                                                                  .status(FriendRelationshipStatus.ACTIVE)
                                                                  .createdAt(LocalDateTime.now())
                                                                  .updatedAt(LocalDateTime.now())
                                                                  .build();

        friendRepository.save(friendRelationship);
    }

    private void deleteFriendRelation(Member member, Member friend) {
        // 친구 관계 삭제
        FriendRelationship friendRelationship = friendRepository.findByMemberAndFriend(member, friend)
                                                                .orElseThrow(() -> new FriendException(
                                                                        ErrorCode.FRIEND_NOT_FOUND));

        friendRepository.delete(friendRelationship);
    }
}
