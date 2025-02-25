package com.cupid.qufit.global.utils.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//import org.springframework.data.elasticsearch.annotations.Mapping;
//import org.springframework.data.elasticsearch.annotations.Setting;

//@Document(indexName = "video")
//@Setting(settingPath = "/index/video/video-settings.json")
//@Mapping(mappingPath = "/index/video/video-mappings.json")
@Getter
@Setter
@Builder
public class ESVideoRoom {

    @Id
    @JsonProperty("video_room_id")
    private String id;

//    @Field(type = FieldType.Keyword)
    private List<String> participants;

//    @Field(type = FieldType.Keyword)
    @JsonProperty("room_tags")
    private List<String> roomTags;
}
