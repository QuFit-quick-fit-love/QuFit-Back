package com.cupid.qufit.domain.video.service;

import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.video.dto.VideoRoomRequest;
import com.cupid.qufit.domain.video.dto.VideoRoomResponse;
import com.cupid.qufit.domain.video.repository.VideoRoomParticipantRepository;
import com.cupid.qufit.domain.video.repository.VideoRoomRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.video.VideoRoom;
import com.cupid.qufit.entity.video.VideoRoomParticipant;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class VideoRoomServiceImpl implements VideoRoomService {

    private final VideoRoomRepository videoRoomRepository;
    private final MemberRepository memberRepository;
    private final VideoRoomParticipantRepository videoRoomParticipantRepository;

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

    @Override
    public VideoRoomResponse createVideoRoom(VideoRoomRequest videoRoomRequest) {
        // ! 1. 입력받은 방 제목, 방 인원 수, 태그를 통해 방 생성 및 DB 저장
        VideoRoom videoRoom = VideoRoomRequest.to(videoRoomRequest);
        videoRoomRepository.save(videoRoom);

        // ! 2. 본인 참가를 위한 joinVideoRoom 을 통해 토큰 생성
        String token = joinVideoRoom(videoRoom.getVideoRoomId(), videoRoomRequest);
        return VideoRoomResponse.from(videoRoom, token);
    }

    /**
     *
     */
    @Override
    public String joinVideoRoom(Long videoRoomId, VideoRoomRequest videoRoomRequset) {
        // ! 1. 방 찾기
        VideoRoom videoRoom = videoRoomRepository.findById(videoRoomId)
                                                 .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // ! 2. 멤버 찾기
        Member member = memberRepository.findById(videoRoomRequset.getParticipantId())
                                        .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // ! 3. 참가자 업데이트
        VideoRoomParticipant newParticipant = VideoRoomParticipant.builder()
                                                                  .videoRoom(videoRoom)
                                                                  .joinedAt(LocalDateTime.now())
                                                                  .member(member)
                                                                  .build();

        // ! 4. 방에 참가자 추가
        videoRoom.getParticipants().add(newParticipant);

        // ! 5. 방 정보 업데이트
        if (newParticipant.getMember().getGender() == 'm') {
            videoRoom.setCurMCount(videoRoom.getCurMCount() + 1);
        } else if (newParticipant.getMember().getGender() == 'f') {
            videoRoom.setCurMCount(videoRoom.getCurWCount() + 1);
        }
        videoRoomRepository.save(videoRoom);

        // ! 6. 토큰 생성
        AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
        token.setName(videoRoomRequset.getParticipantId().toString());
        token.setIdentity(videoRoomRequset.getParticipantId().toString());
        token.addGrants(new RoomJoin(true), new RoomName(videoRoomId.toString()));

        return token.toJwt();
    }
}