package com.cupid.qufit.domain.chat.dto;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.chat.ChatMessage;
import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "채팅방ID")
    private Long chatRoomId;
    @Schema(description = "상대방 회원의 ID")
    private Long otherMemberId;
    @Schema(description = "상대방 닉네임")
    private String otherMemberNickname;
    @Schema(description = "마지막 메시지")
    private String lastMessage;
    @Schema(description = "마지막 메시지ID")
    private String lastMessageId;
    @Schema(description = "마지막 메시지 보낸 시간")
    private LocalDateTime lastMessageTime;
    @Schema(description = "안 읽은 메시지 개수")
    private int unreadCount;
    @Schema(description = "마지막으로 읽은 메시지ID")
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


