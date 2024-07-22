package com.cupid.qufit.entity.chat;

import com.cupid.qufit.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member1_id")
    private Member member1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member2_id")
    private Member member2;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String lastMessage;

    private String lastMessageId;

    private LocalDateTime lastMessageTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ChatRoomStatus status = ChatRoomStatus.ACTIVE;

    private LocalDateTime statusChangedAt;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();


    private boolean isActive() {
        return status == ChatRoomStatus.ACTIVE;
    }

    public Member getOtherMember(Member currentMember) {
        return currentMember.equals(member1) ? member2 : member1;
    }

//    public void updateLastMessage(String messageId, String content, LocalDateTime timestamp) {
//        this.lastMessageId = messageId;
//        this.lastMessage = content;
//        this.lastMessageTime = timestamp;
//    }
}
