package com.cupid.qufit.global.utils.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "participants")
@Setting(settingPath = "/index/participant/participant-settings.json")
@Mapping(mappingPath = "/index/participant/participant-mappings.json")
@Getter @Setter @Builder
public class ESParticipant {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    @JsonProperty("participant_id")
    private Long participantId;

    @Field(type = FieldType.Keyword)
    @JsonProperty("video_room_id")
    private Long videoRoomId;

    @Field(type = FieldType.Date)
    @JsonProperty("updated_at")
    private Date updatedAt;

    @Field(type = FieldType.Keyword)
    @JsonProperty("mbti")
    private String MBTI;

    @Field(type = FieldType.Keyword)
    private List<String> personalities;

    @Field(type = FieldType.Keyword)
    private List<String> hobbies;

    @Field(type = FieldType.Keyword)
    private String location;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Keyword)
    private String gender;

    @Field(type = FieldType.Text)
    private String bio;


}
