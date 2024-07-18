package com.cupid.qufit.entity;

import com.cupid.qufit.entity.chat.ChatRoom;
import com.cupid.qufit.entity.chat.ChatRoomMember;
import jakarta.annotation.Nullable;
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
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", unique = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @NotNull(message = "email은 null일 수 없습니다.")
    private String email;
    @Nullable
    private String password;
    @NotNull(message = "닉네임은 null일 수 없습니다.")
    private String nickname;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @NotNull(message = "생년월일는 null일 수 없습니다.")
    private LocalDate birthDate;
    @NotNull(message = "성별은 null일 수 없습니다.")
    private String gender;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus status = MemberStatus.PENDING;
    @NotNull(message = "자기소개는 null일 수 없습니다.")
    private String bio;
    @Nullable
    private String profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbti_tag_id", unique = false)
    private Tag mbti;

    @OneToMany(mappedBy = "memberHobby", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberHobby> memberHobbies = new ArrayList<>();

    @OneToMany(mappedBy = "memberPersonality", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPersonality> memberPersonalities = new ArrayList<>();

    // ! 채팅 관련 관계 설정
    @OneToMany(mappedBy = "member1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomAsMember1 = new ArrayList<>();

    @OneToMany(mappedBy = "member2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomAsMember2 = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();
}
