package com.cupid.qufit.domain.chat.dto;

import com.cupid.qufit.entity.chat.ChatMessage;
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
    private long unreadCount;
    private int totalPages;
    private long totalElements;

    public static ChatRoomMessageResponse of(List<ChatMessageDTO> messages, long unreadCount,
                                             Page<ChatMessage> messagePage) {
        return ChatRoomMessageResponse.builder()
                                      .messages(messages)
                                      .unreadCount(unreadCount)
                                      .totalPages(messagePage.getTotalPages())
                                      .totalElements(messagePage.getTotalElements())
                                      .build();
    }

}
