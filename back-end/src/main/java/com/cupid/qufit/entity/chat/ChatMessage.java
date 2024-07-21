package com.cupid.qufit.entity.chat;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id; // ! jakarta @Id 쓰면 안되고 Spring이 제공해주는 걸로
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;
    private LocalDateTime readTime; // ? 읽은 시간 일단 필요할 수도 있으니 넣어두고 진행
    @Indexed(expireAfterSeconds = 604800) // ! 7일 후 만료
    private LocalDateTime createdAt; // ! 혹시 나중 자동 삭제 기능을 위해

}
