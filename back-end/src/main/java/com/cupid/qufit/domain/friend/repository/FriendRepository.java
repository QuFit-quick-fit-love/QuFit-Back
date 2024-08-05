package com.cupid.qufit.domain.friend.repository;

import com.cupid.qufit.entity.FriendRelationship;
import com.cupid.qufit.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendRelationship, Long> {

    boolean existsByMemberAndFriend(Member member, Member friend);
}
