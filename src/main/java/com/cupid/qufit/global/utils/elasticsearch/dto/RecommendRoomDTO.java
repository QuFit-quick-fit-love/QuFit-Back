package com.cupid.qufit.global.utils.elasticsearch.dto;

import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.TypeProfiles;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RecommendRoomDTO {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        private String location;
        private Integer birthYear;
        private int typeAgeMax;
        private int typeAgeMin;
        private List<String> MBTIs;
        private List<String> personalities;
        private List<String> hobbies;
        private String gender;

        public static RecommendRoomDTO.Request toRecommendRequest(Member member, TypeProfiles typeProfiles) {
            return RecommendRoomDTO.Request.builder()
                                           .location(member.getLocation().getSi())
                                           .birthYear(member.getBirthDate().getYear())
                                           .typeAgeMax(typeProfiles.getTypeAgeMax() == null ? 0 : typeProfiles.getTypeAgeMax())
                                           .typeAgeMin(typeProfiles.getTypeAgeMin() == null ? 0 : typeProfiles.getTypeAgeMin())
                                           .MBTIs(typeProfiles.getTypeMBTIs().stream().map(typeMBTI -> typeMBTI.getTag().getTagName())
                                                               .toList())
                                           .personalities(typeProfiles.getTypePersonalities().stream().map(typePersonality -> typePersonality.getTag().getTagName())
                                                                     .toList())
                                           .hobbies(typeProfiles.getTypeHobbies().stream().map(typeHobby -> typeHobby.getTag().getTagName())
                                                               .toList())
                                           .gender(member.getGender().toString())
                                           .build();
        }
    }

}
