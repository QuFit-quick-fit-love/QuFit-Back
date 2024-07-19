package com.cupid.qufit.domain.member.dto;

import com.cupid.qufit.entity.Location;
import com.cupid.qufit.entity.MemberHobby;
import com.cupid.qufit.entity.MemberPersonality;
import com.cupid.qufit.entity.Tag;
import com.cupid.qufit.entity.TypeHobby;
import com.cupid.qufit.entity.TypeMBTI;
import com.cupid.qufit.entity.TypePersonality;
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
        private String nickname;
        private Long memberLocationId;
        private int birthYear;
        private String gender;
        private String bio;
        private Long memberMBTITagId;
        private List<Long> memberHobbyTagIds;
        private List<Long> memberPersonalityTagIds;

        private Long typeLocationId;
        private Integer typeAgeMax;
        private Integer typeAgeMin;
        private List<Long> typeMBTITagIds;
        private List<Long> typeHobbyTagIds;
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
