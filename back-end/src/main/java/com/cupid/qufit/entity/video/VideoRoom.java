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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    private String name;

    @Enumerated(EnumType.STRING)
    private VideoRoomStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
    @Max(value = 8, message = "최대 8명까지만 허용됩니다.")
    private int maxParticipants;

    @Min(value = 0, message = "현재 남자 인원은 0명 이상이어야 합니다.")
    @Max(value = 4, message = "현재 남자 인원은 최대 4명까지 가능합니다.")
    private int curMCount;

    @Min(value = 0, message = "현재 여자 인원은 0명 이상이어야 합니다.")
    @Max(value = 4, message = "현재 여자 인원은 최대 4명까지 가능합니다.")
    private int curWCount;

    @OneToMany(mappedBy = "activeRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoRoomParticipant> participants = new ArrayList<>();
}
