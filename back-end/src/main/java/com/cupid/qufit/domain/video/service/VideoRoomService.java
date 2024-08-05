package com.cupid.qufit.domain.video.service;

import com.cupid.qufit.domain.video.dto.VideoRoomDTO;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface VideoRoomService {

    VideoRoomDTO.BasicResponse createVideoRoom(VideoRoomDTO.Request videoRoomRequest, Long memberId);

    String joinVideoRoom(Long videoRoomId, Long memberId);

    VideoRoomDTO.BaseResponse updateVideoRoom(Long videoRoomId, VideoRoomDTO.Request videoRoomRequest);

    void deleteVideoRoom(Long videoRoomId);

    int leaveVideoRoom(Long videoRoomId, Long memberId);

    VideoRoomDTO.DetailResponse getVideoRoomDetail(Long videoRoomId);

    Map<String, Object> getVideoRoomList(Pageable pageable);

    Map<String, Object> getVideoRoomListWithFilter(int page, int size, List<Long> tagIds);

    Map<String, Object> getRecommendedVideoRoomList(int page, Long memberId);
}
