package com.cupid.qufit.domain.member.dto;

import com.cupid.qufit.entity.Location;
import com.cupid.qufit.entity.MemberHobby;
import com.cupid.qufit.entity.MemberPersonality;
import com.cupid.qufit.entity.Tag;
import com.cupid.qufit.entity.TypeHobby;
import com.cupid.qufit.entity.TypeMBTI;
import com.cupid.qufit.entity.TypePersonality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* * 회원가입 DTO
* */

public class MemberSignupDTO {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class request{
    	@NotBlank(message = "닉네임을 입력해주세요.")
    	@Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        private String nickname;
    	@NotNull(message = "지역을 입력해주세요.")
        private Long memberLocationId;
    	@NotNull(message = "태어난 연도를 입력해주세요.")
        private int birthYear;
    	@NotBlank(message = "성별을 입력해주세요.")
        private String gender;
    	@NotBlank(message = "자기소개를 입력해주세요.")
    	@Size(min = 10, message = "자기소개는 10자 이상 입력해주세요.")
        private String bio;

        private Long memberMBTITagId;
        @NotNull(message = "본인 취미를 입력해주세요.")
        private List<Long> memberHobbyTagIds;
        @NotNull(message = "본인 성격을 입력해주세요.")
        private List<Long> memberPersonalityTagIds;

        @NotNull(message = "이상형 지역을 입력해주세요.")
        private Long typeLocationId;
        @NotNull(message = "원하는 나이차를 입력해주세요.")
        private Integer typeAgeMax;
        @NotNull(message = "원하는 나이차를 입력해주세요.")
        private Integer typeAgeMin;

        private List<Long> typeMBTITagIds;
        @NotNull(message = "이상형 취미를 입력해주세요.")
        private List<Long> typeHobbyTagIds;
        @NotNull(message = "이상형 성격을 입력해주세요.")
        private List<Long> typePersonalityTagIds;
    }
    @Getter
    @Builder
    public static class response {
        private String email;
        private String nickname;
        private Location memberLocation;
        private LocalDate birthDate;
        private String gender;
        private String bio;
        private Tag memberMBTITag;
        private List<MemberHobby> memberHobbyTags;
        private List<MemberPersonality> memberPersonalityTags;

        private Location typeLocation;
        private Integer typeAgeMax;
        private Integer typeAgeMin;
        private List<TypeMBTI> typeMBTITags;
        private List<TypeHobby> typeHobbyTags;
        private List<TypePersonality> typePersonalityTags;
    }
}
