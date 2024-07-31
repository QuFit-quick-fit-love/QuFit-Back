package com.cupid.qufit.domain.chat.dto;

import com.cupid.qufit.entity.chat.ChatMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class ChatRoomMessageResponse {

    private List<ChatMessageDTO> messages;
    @JsonIgnore
    private Long unreadCount;
    @JsonIgnore
    private Boolean hasMore;
    @JsonIgnore
    private String firstMessageId;
    @JsonIgnore
    private String lastMessageId;

    public static ChatRoomMessageResponse of(List<ChatMessageDTO> messages, Long unreadCount,
                                              Boolean hasMore, String firstMessageId, String lastMessageId) {
        return ChatRoomMessageResponse.builder()
                                      .messages(messages)
                                      .unreadCount(unreadCount)
                                      .firstMessageId(firstMessageId)
                                      .lastMessageId(lastMessageId)
                                      .hasMore(hasMore)
                                      .build();
    }

}
