package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
