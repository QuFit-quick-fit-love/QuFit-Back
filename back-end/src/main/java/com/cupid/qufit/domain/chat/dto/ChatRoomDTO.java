package com.cupid.qufit.domain.chat.dto;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

    private Long chatRoomId;
    private Long otherMemberId;
    private String otherMemberNickname;
    private String lastMessage;
    private String lastMessageId;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private String lastReadMessageId; // ! 가장 마지막으로 읽은 메세지 ID

    public static ChatRoomDTO from(ChatRoom chatRoom, ChatRoomMember currentMember, Member otherMember) {
        return ChatRoomDTO.builder()
                          .chatRoomId(chatRoom.getId())
                          .otherMemberId(otherMember.getId())
                          .otherMemberNickname(otherMember.getNickname())
                          .lastMessage(chatRoom.getLastMessage())
                          .lastMessageId(chatRoom.getLastMessageId())
                          .lastMessageTime(chatRoom.getLastMessageTime())
                          .unreadCount(currentMember.getUnreadCount())
                          .lastReadMessageId(currentMember.getLastReadMessageId())
                          .build();
    }

}


