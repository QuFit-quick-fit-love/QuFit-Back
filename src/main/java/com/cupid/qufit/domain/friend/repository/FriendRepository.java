package com.cupid.qufit.domain.friend.repository;

import com.cupid.qufit.entity.FriendRelationship;
import com.cupid.qufit.entity.FriendRelationshipStatus;
import com.cupid.qufit.entity.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendRelationship, Long> {

    Optional<FriendRelationship> findByMemberAndFriend(Member member, Member friend);

    Page<FriendRelationship> findByMemberIdAndStatus(Long memberId, FriendRelationshipStatus friendRelationshipStatus,
                                                     Pageable pageable);
}
