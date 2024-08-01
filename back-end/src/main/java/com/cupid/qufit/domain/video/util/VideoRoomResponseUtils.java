package com.cupid.qufit.domain.video.util;

import com.cupid.qufit.entity.video.VideoRoomHobby;
import com.cupid.qufit.entity.video.VideoRoomPersonality;
import java.util.ArrayList;
import java.util.List;

public class VideoRoomResponseUtils {
    public static List<String> toVideoRoomHobbiesList(List<VideoRoomHobby> hobbies) {
        List<String> participantHobbies = new ArrayList<>();
        for (VideoRoomHobby videoRoomHobby : hobbies) {
            participantHobbies.add(videoRoomHobby.getTag().getTagName());
        }
        return participantHobbies;
    }

    public static List<String> toVideoRoomPersonalitiesList(List<VideoRoomPersonality> personalities) {
        List<String> participantPersonalities = new ArrayList<>();
        for (VideoRoomPersonality videoRoomPersonality : personalities) {
            participantPersonalities.add(videoRoomPersonality.getTag().getTagName());
        }
        return participantPersonalities;
    }
}
