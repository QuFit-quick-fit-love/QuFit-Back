package com.cupid.qufit.domain.friend.repository;

import com.cupid.qufit.entity.FriendRelationship;
import com.cupid.qufit.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendRelationship, Long> {

    boolean existsByMemberAndFriend(Member member, Member friend);

    Optional<FriendRelationship> findByMemberAndFriend(Member member, Member friend);
}
