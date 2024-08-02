package com.cupid.qufit.global.utils.elasticsearch.dto;

import co.elastic.clients.elasticsearch._types.FieldValue;
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
    }

    @Getter
    @Setter
    @Builder
    public static class Response {
    }
}
