package com.cupid.qufit.entity.video;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private VideoRoomStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    private int maxParticipants;

    private int curMCount;

    private int curWCount;

    @OneToMany(mappedBy = "videoRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoRoomParticipant> participants = new ArrayList<>();
}
