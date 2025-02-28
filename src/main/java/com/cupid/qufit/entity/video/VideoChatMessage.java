package com.cupid.qufit.entity.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "video_chat_messages")
public class VideoChatMessage{

    @Id
    private Long id;
    private Long roomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
}
