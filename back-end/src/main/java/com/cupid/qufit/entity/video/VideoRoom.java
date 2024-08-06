package com.cupid.qufit.entity.video;

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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class VideoRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoRoomId;

    @Column(nullable = false, length = 30)
    private String videoRoomName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VideoRoomStatus status = VideoRoomStatus.READY;

    @CreatedDate
    private LocalDateTime createdAt;

    private int maxParticipants;

    @Builder.Default
    private int curMCount = 0;

    @Builder.Default
    private int curWCount = 0;

    @OneToMany(mappedBy = "videoRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoRoomParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "videoRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoRoomHobby> videoRoomHobby = new ArrayList<>();

    @OneToMany(mappedBy = "videoRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoRoomPersonality> videoRoomPersonality = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Member host;
}
