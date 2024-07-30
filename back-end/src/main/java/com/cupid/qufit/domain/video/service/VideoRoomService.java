package com.cupid.qufit.domain.video.service;

import com.cupid.qufit.domain.video.dto.VideoRoomRequest;
import com.cupid.qufit.domain.video.dto.VideoRoomResponse;
import java.util.List;

public interface VideoRoomService {

    VideoRoomResponse createVideoRoom(VideoRoomRequest videoRoomRequset);

    String joinVideoRoom(Long videoRoomId, VideoRoomRequest videoRoomRequset);

    VideoRoomResponse updateVideoRoom(Long videoRoomId, VideoRoomRequest videoRoomRequest);

    void deleteVideoRoom(Long videoRoomId);

    void leaveVideoRoom(Long videoRoomId, Long participantId);

    VideoRoomResponse getVideoRoomDetail(Long videoRoomId);

    List<VideoRoomResponse> getVideoRoomList();
}
