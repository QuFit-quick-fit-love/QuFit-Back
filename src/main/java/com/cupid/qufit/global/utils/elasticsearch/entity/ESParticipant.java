package com.cupid.qufit.global.utils.elasticsearch.entity;

import com.cupid.qufit.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//import org.springframework.data.elasticsearch.annotations.Mapping;
//import org.springframework.data.elasticsearch.annotations.Setting;

//@Document(indexName = "participants")
//@Setting(settingPath = "/index/participant/participant-settings.json")
//@Mapping(mappingPath = "/index/participant/participant-mappings.json")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ESParticipant {

    @Id
//    @Field(type = FieldType.Keyword)
    private String id;

//    @Field(type = FieldType.Keyword)
    private String participantId;

//    @Field(type = FieldType.Keyword)
    private String videoRoomId;

//    @Field(type = FieldType.Keyword)
    private String MBTI;

//    @Field(type = FieldType.Keyword)
    private List<String> personalities;

//    @Field(type = FieldType.Keyword)
    private List<String> hobbies;

//    @Field(type = FieldType.Keyword)
    private String location;

//    @Field(type = FieldType.Integer)
    private Integer birthYear;

//    @Field(type = FieldType.Keyword)
    private String gender;

//    @Field(type = FieldType.Text)
    private String bio;

    public static ESParticipant createParticipant(String videoRoomId, Member member) {
        return ESParticipant.builder()
                .id(member.getId().toString())
                .participantId(member.getId().toString())
                .videoRoomId(videoRoomId)
                .MBTI(member.getMBTI().getTagName())
                .personalities(member.getMemberPersonalities().stream()
                        .map(pTag -> pTag.getTag().getTagName())
                        .collect(Collectors.toList()))
                .hobbies(member.getMemberHobbies().stream()
                        .map(hTag -> hTag.getTag().getTagName())
                        .collect(Collectors.toList()))
                .location(member.getLocation().getSi())
                .birthYear(member.getBirthDate().getYear())
                .gender(member.getGender().toString())
                .bio(member.getBio())
                .build();
    }
}
