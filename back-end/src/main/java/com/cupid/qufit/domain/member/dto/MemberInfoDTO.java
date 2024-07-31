package com.cupid.qufit.domain.member.dto;

import static com.cupid.qufit.domain.member.util.MemberBirthDateUtil.convertToLocalDate;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.entity.TypeProfiles;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "[ MemberInfoDTO ] 회원 가입, 회원 정보 요청 DTO")
    public static class Request {

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        @Schema(description = "닉네임 : 2자 이상 10자 이하, 빈칸 허용 x")
        private String nickname;
        @NotNull(message = "지역을 입력해주세요.")
        @Schema(description = "지역 코드 : null 허용 x")
        private Long locationId;
        @NotNull(message = "태어난 연도를 입력해주세요.")
        @Range(min = 1900, max = 2100, message = "올바른 연도를 입력하세요.")
        @Schema(description = "지역 코드 : null 허용 x")
        private Integer birthYear;
        @NotBlank(message = "성별을 입력해주세요.")
        @Pattern(regexp = "[mf]$", message = "올바른 성별을 입력하세요. (m 또는 f)")
        @Schema(description = "성별 : m 또는 f")
        private String gender;
        @NotBlank(message = "자기소개를 입력해주세요.")
        @Size(min = 10, message = "자기소개는 10자 이상 입력해주세요.")
        @Schema(description = "자기소개 : 10자 이상")
        private String bio;
        @Schema(description = "자신의 mbti 태그 이름 : null 허용")
        private String memberMBTITag;
        @NotEmpty(message = "본인 취미를 입력해주세요.")
        @Schema(description = "자신의 취미 태그 이름 list : null 허용 x")
        private List<String> memberHobbyTags;
        @NotEmpty(message = "본인 성격을 입력해주세요.")
        @Schema(description = "자신의 성격 태그 이름 list : null 허용 x")
        private List<String> memberPersonalityTags;

        @NotNull(message = "원하는 나이차를 입력해주세요.")
        @Min(value = 0, message = "올바른 나이차를 입력하세요.")
        @Schema(description = "원하는 나이차(+) : 0이상")
        private Integer typeAgeMax;
        @NotNull(message = "원하는 나이차를 입력해주세요.")
        @Min(value = 0, message = "올바른 나이차를 입력하세요.")
        @Schema(description = "원하는 나이차(-) : 0이상")
        private Integer typeAgeMin;
        @Schema(description = "이상형 mbti 태그 이름 list : null 허용")
        private List<String> typeMBTITags;
        @NotEmpty(message = "이상형 취미를 입력해주세요.")
        @Schema(description = "이상형 취미 태그 이름 list : null 허용 x")
        private List<String> typeHobbyTags;
        @NotEmpty(message = "이상형 성격을 입력해주세요.")
        @Schema(description = "이상형 성격 태그 이름 list : null 허용 x")
        private List<String> typePersonalityTags;

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
    @Schema(description = "[ MemberInfoDTO ] 회원 가입, 회원 정보 응답 DTO")
    public static class Response {

        @Schema(description = "회원 식별자")
        private Long memberId;
        @Schema(description = "회원 이메일")
        private String email;
        @Schema(description = "회원 닉네임")
        private String nickname;
        @Schema(description = "회원 지역 이름")
        private String location;
        @Schema(description = "회원 태어난 연도")
        private Integer birthYear;
        @Schema(description = "회원 성별(m/f)")
        private Character gender;
        @Schema(description = "회원 자기소개")
        private String bio;
        @Schema(description = "회원 프로필 사진 url (없을 때 : ''로 반환)")
        private String profileImage;
        @Schema(description = "회원 mbti tag 이름")
        private String memberMBTITag;
        @Schema(description = "회원 취미 tag 이름 list")
        private List<String> memberHobbyTags;
        @Schema(description = "회원 성격 tag 이름 list")
        private List<String> memberPersonalityTags;
        @Schema(description = "원하는 성격차(+)")
        private Integer typeAgeMax;
        @Schema(description = "원하는 성격차(-)")
        private Integer typeAgeMin;
        @Schema(description = "이상형 mbti tag 이름 list")
        private List<String> typeMBTI;
        @Schema(description = "이상형 취미 tag 이름 list")
        private List<String> typeHobbyTags;
        @Schema(description = "이상형 성격 tag 이름 list")
        private List<String> typePersonalityTags;

        public static MemberInfoDTO.Response of(Member member, TypeProfiles typeProfiles) {
            return MemberInfoDTO.Response.builder()
                                         .memberId(member.getId())
                                         .email(member.getEmail())
                                         .nickname(member.getNickname())
                                         .location(member.getLocation().getSi())
                                         .birthYear(member.getBirthDate().getYear())
                                         .gender(member.getGender())
                                         .bio(member.getBio())
                                         .profileImage(member.getProfileImage())
                                         .memberMBTITag(member.getMBTI() != null ? member.getMBTI().getTagName() : null)
                                         .memberHobbyTags(member.getMemberHobbies().stream()
                                                                .map(memberHobby -> memberHobby.getTag().getTagName())
                                                                .collect(Collectors.toList()))
                                         .memberPersonalityTags(member.getMemberPersonalities().stream()
                                                                      .map(memberPersonality -> memberPersonality.getTag()
                                                                                                                 .getTagName())
                                                                      .collect(Collectors.toList()))
                                         .typeAgeMax(typeProfiles.getTypeAgeMax())
                                         .typeAgeMin(typeProfiles.getTypeAgeMin())
                                         .typeMBTI(!typeProfiles.getTypeMBTIs().isEmpty() ? typeProfiles.getTypeMBTIs()
                                                                                                        .stream()
                                                                                                        .map(typeMBTI -> typeMBTI.getTag()
                                                                                                                                 .getTagName())
                                                                                                        .collect(
                                                                                                                Collectors.toList())
                                                                                          : null)
                                         .typeHobbyTags(typeProfiles.getTypeHobbies().stream()
                                                                    .map(typeHobby -> typeHobby.getTag().getTagName())
                                                                    .collect(Collectors.toList()))
                                         .typePersonalityTags(typeProfiles.getTypePersonalities().stream()
                                                                          .map(typePersonality -> typePersonality.getTag()
                                                                                                                 .getTagName())
                                                                          .collect(Collectors.toList()))
                                         .build();
        }
    }
}
