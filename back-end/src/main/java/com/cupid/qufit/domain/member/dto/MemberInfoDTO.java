package com.cupid.qufit.domain.member.dto;

import static com.cupid.qufit.domain.member.util.MemberBirthDateUtil.convertToLocalDate;
import com.cupid.qufit.entity.Location;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberHobby;
import com.cupid.qufit.entity.MemberPersonality;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.entity.Tag;
import com.cupid.qufit.entity.TypeHobby;
import com.cupid.qufit.entity.TypeMBTI;
import com.cupid.qufit.entity.TypePersonality;
import com.cupid.qufit.entity.TypeProfiles;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Data
public class MemberInfoDTO {

/*
* * 회원 가입, 회원 정보 수정 요청 DTO
* */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        private String nickname;
        @NotNull(message = "지역을 입력해주세요.")
        private Long locationId;
        @NotNull(message = "태어난 연도를 입력해주세요.")
        @Range(min = 1900, max = 2100, message = "올바른 연도를 입력하세요.")
        private Integer birthYear;
        @NotBlank(message = "성별을 입력해주세요.")
        @Pattern(regexp = "[mf]$", message = "올바른 성별을 입력하세요. (m 또는 f)")
        private String gender;
        @NotBlank(message = "자기소개를 입력해주세요.")
        @Size(min = 10, message = "자기소개는 10자 이상 입력해주세요.")
        private String bio;

        private Long memberMBTITagId;
        @NotEmpty(message = "본인 취미를 입력해주세요.")
        private List<Long> memberHobbyTagIds;
        @NotEmpty(message = "본인 성격을 입력해주세요.")
        private List<Long> memberPersonalityTagIds;

        @NotNull(message = "원하는 나이차를 입력해주세요.")
        @Min(value = 0, message = "올바른 나이차를 입력하세요.")
        private Integer typeAgeMax;
        @NotNull(message = "원하는 나이차를 입력해주세요.")
        @Min(value = 0, message = "올바른 나이차를 입력하세요.")
        private Integer typeAgeMin;

        private List<Long> typeMBTITagIds;
        @NotEmpty(message = "이상형 취미를 입력해주세요.")
        private List<Long> typeHobbyTagIds;
        @NotEmpty(message = "이상형 성격을 입력해주세요.")
        private List<Long> typePersonalityTagIds;

        /*
         * * 이메일과 부가정보로 Member 엔티티 생성
         *
         * @param : 카카오 회원 계정 이메일, 부가정보 DTO
         * */
        public static Member toEntity(String email, MemberInfoDTO.Request request) {
            return Member.builder()
                         .location(null)
                         .role(MemberRole.USER)
                         .email(email)
                         .password("")
                         .nickname(request.getNickname())
                         .birthDate(convertToLocalDate(request.getBirthYear()))
                         .gender(request.getGender().charAt(0))
                         .bio(request.getBio())
                         .profileImage("")
                         .MBTI(null)
                         .build();
        }
    }

    /*
     * * 회원정보 조회 응답 DTO
     * */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long memberId;
        private String email;
        private String nickname;
        private Location location;
        private Integer birthYear;
        private Character gender;
        private String bio;
        private Tag memberMBTITag;
        private List<Tag> memberHobbyTags;
        private List<Tag> memberPersonalityTags;

        private Integer typeAgeMax;
        private Integer typeAgeMin;
        private List<Tag> typeMBTI;
        private List<Tag> typeHobby;
        private List<Tag> typePersonality;

        public static MemberInfoDTO.Response of(Member member, TypeProfiles typeProfiles) {
            return MemberInfoDTO.Response.builder()
                                .memberId(member.getId())
                                .email(member.getEmail())
                                .nickname(member.getNickname())
                                .location(member.getLocation())
                                .birthYear(member.getBirthDate().getYear())
                                .gender(member.getGender())
                                .bio(member.getBio())
                                .memberMBTITag(member.getMBTI())
                                .memberHobbyTags(member.getMemberHobbies().stream()
                                                       .map(MemberHobby::getTag)
                                                       .collect(Collectors.toList()))
                                .memberPersonalityTags(member.getMemberPersonalities().stream()
                                                             .map(MemberPersonality::getTag)
                                                             .collect(Collectors.toList()))
                                .typeAgeMax(typeProfiles.getTypeAgeMax())
                                .typeAgeMin(typeProfiles.getTypeAgeMin())
                                .typeMBTI(typeProfiles.getTypeMBTIs().stream()
                                                      .map(TypeMBTI::getTag)
                                                      .collect(Collectors.toList()))
                                .typeHobby(typeProfiles.getTypeHobbies().stream()
                                                       .map(TypeHobby::getTag)
                                                       .collect(Collectors.toList()))
                                .typePersonality(typeProfiles.getTypePersonalities().stream()
                                                             .map(TypePersonality::getTag)
                                                             .collect(Collectors.toList()))
                                .build();
        }
    }
}
