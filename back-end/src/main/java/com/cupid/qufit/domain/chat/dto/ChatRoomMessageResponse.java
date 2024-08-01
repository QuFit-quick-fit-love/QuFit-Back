package com.cupid.qufit.domain.chat.dto;

import com.cupid.qufit.entity.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomMessageResponse {

    private List<ChatMessageDTO> messages;
    private Long unreadCount;
    private Boolean hasMore;
    private String firstMessageId;
    private String lastMessageId;
    private Boolean isUnreadCountExceeded;

    public static ChatRoomMessageResponse of(List<ChatMessageDTO> messages, Long unreadCount,
                                              Boolean hasMore, String firstMessageId, String lastMessageId, Boolean isUnreadCountExceeded) {
        return ChatRoomMessageResponse.builder()
                                      .messages(messages)
                                      .unreadCount(unreadCount)
                                      .hasMore(hasMore)
                                      .firstMessageId(firstMessageId)
                                      .lastMessageId(lastMessageId)
                                      .isUnreadCountExceeded(isUnreadCountExceeded)
                                      .build();
    }

}
