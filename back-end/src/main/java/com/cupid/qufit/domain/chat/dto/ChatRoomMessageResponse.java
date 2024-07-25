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
    private Long totalElements;
    @JsonIgnore
    private Boolean hasMore;

    public static ChatRoomMessageResponse of(List<ChatMessageDTO> messages, Long unreadCount,
                                             Long totalElements, Boolean hasMore) {
        return ChatRoomMessageResponse.builder()
                                      .messages(messages)
                                      .unreadCount(unreadCount)
                                      .totalElements(totalElements)
                                      .hasMore(hasMore)
                                      .build();
    }

}
