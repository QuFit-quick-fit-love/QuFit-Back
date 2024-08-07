package com.cupid.qufit.domain.video.dto;

import static com.cupid.qufit.domain.video.util.VideoRoomResponseUtils.toVideoRoomHobbiesList;
import static com.cupid.qufit.domain.video.util.VideoRoomResponseUtils.toVideoRoomPersonalitiesList;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberHobby;
import com.cupid.qufit.entity.MemberPersonality;
import com.cupid.qufit.entity.video.VideoRoom;
import com.cupid.qufit.entity.video.VideoRoomParticipant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

public class VideoRoomDTO {

    @Getter
    @Builder
    public static class Request {

        @Schema(description = "미팅방 제목")
        @NotEmpty
        private String videoRoomName; // 방 제목
        @Schema(description = "미팅방 최대 인원수")
        @Size(min = 1, max = 4, message = "인원수는 1:1~4:4 입니다")
        private int maxParticipants; // 최대 인원수
        @Schema(description = "미팅방 취미 태그")
        private List<Long> videoRoomHobbies; // 방 취미 태그
        @Schema(description = "미팅방 성격 태그")
        private List<Long> videoRoomPersonalities; // 방 성격 태그
        @Schema(description = "미팅방 생성 타입(1:대다대방(대기방), 3:일대일방)")
        private int statusType;

        public static VideoRoom to(Request videoRoomRequest) {
            return VideoRoom.builder()
                            .videoRoomName(videoRoomRequest.getVideoRoomName())
                            .createdAt(LocalDateTime.now())
                            .maxParticipants(videoRoomRequest.getMaxParticipants())
                            .build();
        }
    }

    @Getter
    @SuperBuilder
    public static class BaseResponse {

        @Schema(description = "미팅방 id")
        private Long videoRoomId; // 방 id
        @Schema(description = "방 제목")
        private String videoRoomName; // 방 제목
        @Schema(description = "생성 일시")
        private LocalDateTime createdAt; // 생성일시
        @Schema(description = "최대 인원수")
        private int maxParticipants; // 최대 인원수
        @Schema(description = "현재 남자 수")
        private int curMCount; // 현재 남자 수
        @Schema(description = "현재 여자 수")
        private int curWCount; // 현재 여자 수
        @Schema(description = "방장 id")
        private Long hostId;
        @Schema(description = "방 취미 태그")
        private List<String> videoRoomHobby; // 방 취미 태그
        @Schema(description = "방 성격 태그")
        private List<String> videoRoomPersonality; // 방 성격 태그

        public static BaseResponse from(VideoRoom videoRoom) {
            return BaseResponse.builder()
                               .videoRoomId(videoRoom.getVideoRoomId())
                               .videoRoomName(videoRoom.getVideoRoomName())
                               .createdAt(videoRoom.getCreatedAt())
                               .maxParticipants(videoRoom.getMaxParticipants())
                               .curMCount(videoRoom.getCurMCount())
                               .curWCount(videoRoom.getCurWCount())
                               .hostId(videoRoom.getHost().getId())
                               .videoRoomHobby(toVideoRoomHobbiesList(videoRoom.getVideoRoomHobby()))
                               .videoRoomPersonality(
                                       toVideoRoomPersonalitiesList(videoRoom.getVideoRoomPersonality()))
                               .build();
        }
    }

    @Getter
    @SuperBuilder
    public static class BasicResponse extends BaseResponse {

        @Schema(description = "방 참가 토큰")
        private String token; // 방 참가 토큰

        public static BasicResponse from(VideoRoom videoRoom, String token) {
            return BasicResponse.builder()
                                .videoRoomId(videoRoom.getVideoRoomId())
                                .videoRoomName(videoRoom.getVideoRoomName())
                                .createdAt(videoRoom.getCreatedAt())
                                .maxParticipants(videoRoom.getMaxParticipants())
                                .curMCount(videoRoom.getCurMCount())
                                .curWCount(videoRoom.getCurWCount())
                                .hostId(videoRoom.getHost().getId())
                                .videoRoomHobby(toVideoRoomHobbiesList(videoRoom.getVideoRoomHobby()))
                                .videoRoomPersonality(
                                        toVideoRoomPersonalitiesList(videoRoom.getVideoRoomPersonality()))
                                .token(token)
                                .build();
        }
    }

    @Getter
    @SuperBuilder
    public static class DetailResponse extends BaseResponse {

        // 참가자의 성격 및 취미 정보를 담을 필드
        @Schema(description = "참가자들의 취미")
        private List<String> participantHobbies;
        @Schema(description = "참가자들의 성격")
        private List<String> participantPersonalities;
        @Schema(description = "참가자들의 정보")
        private List<MemberInfo> members;

        public static DetailResponse withDetails(VideoRoom videoRoom) {
            // ! 1. 방 참가자 태그들 가져오기
            Map<String, Integer> hobbyCountMap = new HashMap<>();
            Map<String, Integer> personalityCountMap = new HashMap<>();
            List<MemberInfo> members = new ArrayList<>();

            for (VideoRoomParticipant participant : videoRoom.getParticipants()) {
                Member member = participant.getMember();
                members.add(MemberInfo.builder()
                                      .id(member.getId())
                                      .gender(member.getGender())
                                      .nickname(member.getNickname())
                                      .build());
                for (MemberHobby hobby : member.getMemberHobbies()) {
                    String hobbyName = hobby.getTag().getTagName();
                    hobbyCountMap.put(hobbyName, hobbyCountMap.getOrDefault(hobbyName, 0) + 1);
                }
                for (MemberPersonality personality : member.getMemberPersonalities()) {
                    String personalityName = personality.getTag().getTagName();
                    personalityCountMap.put(personalityName, personalityCountMap.getOrDefault(personalityName, 0) + 1);
                }
            }

            // ! 2. 빈도수 기준으로 정렬
            List<String> hobbies = hobbyCountMap.entrySet().stream()
                                                .sorted((e1, e2) -> e2.getValue()
                                                                      .compareTo(e1.getValue()))
                                                .map(Map.Entry::getKey)
                                                .toList();

            List<String> personalities = personalityCountMap.entrySet().stream()
                                                            .sorted((e1, e2) -> e2.getValue().compareTo(
                                                                    e1.getValue()))
                                                            .map(Map.Entry::getKey)
                                                            .toList();

            return DetailResponse.builder()
                                 .videoRoomId(videoRoom.getVideoRoomId())
                                 .videoRoomName(videoRoom.getVideoRoomName())
                                 .createdAt(videoRoom.getCreatedAt())
                                 .maxParticipants(videoRoom.getMaxParticipants())
                                 .curMCount(videoRoom.getCurMCount())
                                 .curWCount(videoRoom.getCurWCount())
                                 .hostId(videoRoom.getHost().getId())
                                 .videoRoomHobby(toVideoRoomHobbiesList(videoRoom.getVideoRoomHobby()))
                                 .videoRoomPersonality(
                                         toVideoRoomPersonalitiesList(videoRoom.getVideoRoomPersonality()))
                                 .participantHobbies(hobbies)
                                 .participantPersonalities(personalities)
                                 .members(members)
                                 .build();
        }

        // MemberInfo 클래스 정의
        @Getter
        @Builder
        @AllArgsConstructor
        public static class MemberInfo {

            private Long id;
            private Character gender;
            private String nickname;
        }
    }

    @Getter
    @Builder
    public static class joinResponse {

        @Schema(description = "방 id")
        private Long videoRoomId;
        @Schema(description = "방 참가 토큰")
        private String token; // 방 참가 토큰
        @Schema(description = "방장 id")
        private Long hostId;

        public static joinResponse from(VideoRoom videoRoom, String token) {
            return joinResponse.builder()
                               .videoRoomId(videoRoom.getVideoRoomId())
                               .token(token)
                               .hostId(videoRoom.getHost().getId())
                               .build();
        }
    }
}
