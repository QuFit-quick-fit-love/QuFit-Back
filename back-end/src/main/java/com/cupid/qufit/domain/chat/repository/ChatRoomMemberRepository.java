package com.cupid.qufit.domain.chat.repository;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import com.cupid.qufit.entity.chat.ChatRoomMemberStatus;
import com.cupid.qufit.entity.chat.ChatRoomStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByMemberAndStatus(Member member, ChatRoomMemberStatus chatRoomStatus);

    Optional<ChatRoomMember> findByChatRoomIdAndMemberId(Long chatRoomId, Long currentMemberId);

    Optional<ChatRoomMember> findByChatRoomAndMember(ChatRoom chatRoom, Member sender);

    List<ChatRoomMember> findByMemberId(Long memberId);

    @Query("SELECT CASE WHEN COUNT(crm) = 0 THEN true ELSE false END " +
            "FROM ChatRoomMember crm " +
            "WHERE crm.chatRoom = :chatRoom AND crm.status <> :status")
    boolean areAllMembersInStatus(ChatRoom chatRoom, ChatRoomMemberStatus status);
}
