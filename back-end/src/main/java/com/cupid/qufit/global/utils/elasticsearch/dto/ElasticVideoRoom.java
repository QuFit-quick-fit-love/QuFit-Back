package com.cupid.qufit.global.utils.elasticsearch.dto;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.video.VideoRoom;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ElasticVideoRoom {

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Request {

        @JsonProperty("video_room_id")
        private Long videoRoomId;
        @JsonProperty("video_room_name")
        private String videoRoomName;
        private Participant participant;

        public static ElasticVideoRoom.Request of(VideoRoom videoRoom, Member member) {
            return Request.builder()
                          .videoRoomId(videoRoom.getVideoRoomId())
                          .videoRoomName(videoRoom.getVideoRoomName())
                          .participant(
                                  Participant.builder()
                                             .memberId(member.getId())
                                             .updatedAt(String.valueOf(member.getUpdatedAt()))
                                             .mbti(member.getMBTI() != null ? member.getMBTI().getTagName() : null)
                                             .personalities(member.getMemberPersonalities().stream()
                                                                  .map(mp -> mp.getTag().getTagName())
                                                                  .collect(Collectors.toList()))
                                             .hobbies(member.getMemberHobbies().stream()
                                                            .map(mh -> mh.getTag().getTagName())
                                                            .collect(Collectors.toList()))
                                             .location(member.getLocation().getSi())
                                             .age(member.getBirthDate().getYear())
                                             .bio(member.getBio())
                                             .gender(member.getGender())
                                             .build()
                          )
                          .build();
        }

        @Getter
        @Setter
        @Builder
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Participant {

            @JsonProperty("member_id")
            private Long memberId;
            @JsonProperty("updated_at")
            private String updatedAt;
            private String mbti;
            private List<String> personalities;
            private List<String> hobbies;
            private String location;
            private Integer age;
            private String bio;
            private Character gender;
        }
    }


}
