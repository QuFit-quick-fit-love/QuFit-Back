package com.cupid.qufit.domain.chat.dto;

import com.cupid.qufit.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private String id;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    public static ChatMessageDTO from(ChatMessage message) {
        return ChatMessageDTO.builder()
                             .id(message.getId())
                             .senderId(message.getSenderId())
                             .content(message.getContent())
                             .timestamp(message.getTimestamp())
                             .isRead(message.isRead())
                             .build();
    }

}
