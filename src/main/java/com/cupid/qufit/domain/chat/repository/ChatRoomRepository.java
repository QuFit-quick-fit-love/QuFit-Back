package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMember1AndMember2OrMember1AndMember2(Member member1, Member member2, Member member3,
                                                                  Member member4);
}