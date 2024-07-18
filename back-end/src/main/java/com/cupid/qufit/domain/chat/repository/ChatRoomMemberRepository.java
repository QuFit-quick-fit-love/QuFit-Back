package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

}
