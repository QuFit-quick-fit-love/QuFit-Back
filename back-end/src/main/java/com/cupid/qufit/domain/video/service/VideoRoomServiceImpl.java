package com.cupid.qufit.domain.video.service;

import com.cupid.qufit.domain.balancegame.service.BalanceGameService;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.member.repository.profiles.TypeProfilesRepository;
import com.cupid.qufit.domain.member.repository.tag.TagRepository;
import com.cupid.qufit.domain.video.dto.VideoRoomDTO;
import com.cupid.qufit.domain.video.dto.VideoRoomDTO.BaseResponse;
import com.cupid.qufit.domain.video.dto.VideoRoomDTO.joinResponse;
import com.cupid.qufit.domain.video.repository.VideoRoomParticipantRepository;
import com.cupid.qufit.domain.video.repository.VideoRoomRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.TypeProfiles;
import com.cupid.qufit.entity.video.VideoRoom;
import com.cupid.qufit.entity.video.VideoRoomHobby;
import com.cupid.qufit.entity.video.VideoRoomParticipant;
import com.cupid.qufit.entity.video.VideoRoomPersonality;
import com.cupid.qufit.entity.video.VideoRoomStatus;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.exception.exceptionType.TagException;
import com.cupid.qufit.global.exception.exceptionType.VideoException;
import com.cupid.qufit.global.utils.elasticsearch.dto.RecommendRoomDTO.Request;
import com.cupid.qufit.global.utils.elasticsearch.entity.ESParticipant;
import com.cupid.qufit.global.utils.elasticsearch.service.ESParticipantServiceImpl;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private final TagRepository tagRepository;
    private final ESParticipantServiceImpl esParticipantService;
    private final TypeProfilesRepository typeProfilesRepository;
    private final BalanceGameService balanceGameService;

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

    /**
     * 방 생성
     */
    @Override
    public VideoRoomDTO.BasicResponse createVideoRoom(VideoRoomDTO.Request videoRoomRequest, Long memberId) {
        // ! 1. 입력받은 방 제목, 방 인원 수, 태그를 통해 방 생성 및 DB 저장
        VideoRoom videoRoom = VideoRoomDTO.Request.to(videoRoomRequest);
        // ! 1.1 방장 설정
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        videoRoom.setHost(member);
        videoRoom.getVideoRoomHobby().addAll(toHobbyList(videoRoomRequest, videoRoom));
        videoRoom.getVideoRoomPersonality().addAll(toPersonalityList(videoRoomRequest, videoRoom));
        videoRoomRepository.save(videoRoom);

        // ! 2. 본인 참가를 위한 joinVideoRoom 을 통해 토큰 생성
        String token = joinVideoRoom(videoRoom.getVideoRoomId(), memberId).getToken();
        return VideoRoomDTO.BasicResponse.from(videoRoom, token);
    }

    /**
     * 방 참가
     */
    @Override
    public joinResponse joinVideoRoom(Long videoRoomId, Long memberId) {
        // ! 1. 방 찾기
        VideoRoom videoRoom = videoRoomRepository.findById(videoRoomId)
                                                 .orElseThrow(() -> new VideoException(ErrorCode.VIDEO_ROOM_NOT_FOUND));

        // ! 2. 멤버 찾기
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // ! 2.1 멤버가 이미 참여중인 방일 경우
        for (VideoRoomParticipant videoRoomParticipant : videoRoom.getParticipants()) {
            if (videoRoomParticipant.getMember().getId().equals(memberId)) {
                throw new VideoException(ErrorCode.PARTICIPANT_ALREADY_EXISTS);
            }
        }

        // ! 3. 참가자 생성
        VideoRoomParticipant newParticipant = VideoRoomParticipant.builder()
                                                                  .videoRoom(videoRoom)
                                                                  .joinedAt(LocalDateTime.now())
                                                                  .member(member)
                                                                  .build();

        /* ! 4. 방에 참가자 추가 */
        // 4-1. DB에 참가자 저장
        videoRoomParticipantRepository.save(newParticipant);
        videoRoom.getParticipants().add(newParticipant);

        // 4-2. ES에 참가자 저장
        ESParticipant newESParticipant = ESParticipant.createParticipant(videoRoomId.toString(), member);
        esParticipantService.save(newESParticipant);

        // ! 5. 방 정보 업데이트
        if (newParticipant.getMember().getGender() == 'm') {
            videoRoom.setCurMCount(videoRoom.getCurMCount() + 1);
        } else if (newParticipant.getMember().getGender() == 'f') {
            videoRoom.setCurWCount(videoRoom.getCurWCount() + 1);
        }
        videoRoomRepository.save(videoRoom);

        // ! 6. 토큰 생성
        AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
        token.setName(memberId.toString());
        token.setIdentity(memberId.toString());
        token.addGrants(new RoomJoin(true), new RoomName(videoRoomId.toString()));

        return VideoRoomDTO.joinResponse.from(videoRoom, token.toJwt());
    }

    /**
     * 방 업데이트
     */
    @Override
    public VideoRoomDTO.BaseResponse updateVideoRoom(Long videoRoomId, VideoRoomDTO.Request videoRoomRequest) {
        // ! 1. 방 찾기
        VideoRoom videoRoom = videoRoomRepository.findById(videoRoomId)
                                                 .orElseThrow(() -> new VideoException(ErrorCode.VIDEO_ROOM_NOT_FOUND));

        /* ! 2. 방 정보 업데이트 (방 제목, 최대 인원 수, 취미, 성격 태그) */

        // ! 2-1. 방 제목, 최대 인원 수 업데이트
        videoRoom.setVideoRoomName(videoRoomRequest.getVideoRoomName());
        videoRoom.setMaxParticipants(videoRoomRequest.getMaxParticipants());

        // ! 2-2. 방 취미 태그 업데이트
        videoRoom.getVideoRoomHobby().clear();
        videoRoom.getVideoRoomHobby().addAll(toHobbyList(videoRoomRequest, videoRoom));

        // ! 2-3. 방 성격 태그 업데이트
        videoRoom.getVideoRoomPersonality().clear();
        videoRoom.getVideoRoomPersonality().addAll(toPersonalityList(videoRoomRequest, videoRoom));

        // ! 3. 방 정보 저장
        videoRoomRepository.save(videoRoom);

        return VideoRoomDTO.BasicResponse.from(videoRoom);
    }

    /**
     * 방 삭제
     */
    @Override
    public void deleteVideoRoom(Long videoRoomId) {
        // ! 1. 방 찾기
        VideoRoom videoRoom = videoRoomRepository.findById(videoRoomId)
                                                 .orElseThrow(() -> new VideoException(ErrorCode.VIDEO_ROOM_NOT_FOUND));

        // !. 2. ES에서 해당 방 참가자 삭제
        esParticipantService.deleteAllByRoomId(videoRoomId.toString());

        // ! 3. DB에 저장되어있는 밸런스 게임 선택 전부 삭제
        balanceGameService.deleteAllChoice(videoRoomId);

        // ! 4. 방 제거
        videoRoomRepository.delete(videoRoom);
    }

    /**
     * 방 떠나기
     */
    @Override
    public int leaveVideoRoom(Long videoRoomId, Long memberId) {
        // ! 1. 방 찾기
        VideoRoom videoRoom = videoRoomRepository.findById(videoRoomId)
                                                 .orElseThrow(() -> new VideoException(ErrorCode.VIDEO_ROOM_NOT_FOUND));

        // ! 2. 방 인원 1명일 경우 방 삭제
        if (videoRoom.getCurMCount() + videoRoom.getCurWCount() == 1) {
            videoRoomRepository.delete(videoRoom);
            return 1;
        }

        // ! 3. 참가자 찾기
        VideoRoomParticipant participant = videoRoom.getParticipants().stream()
                                                    .filter(p -> p.getMember().getId().equals(memberId))
                                                    .findFirst()
                                                    .orElseThrow(
                                                            () -> new VideoException(ErrorCode.PARTICIPANT_NOT_FOUND));

        // ! 4. 방에서 참가자 제거
        // 4-1. DB에서 해당 참가자 삭제
        videoRoomParticipantRepository.delete(participant);
        videoRoom.getParticipants().remove(participant);

        // 4-2. ES에서 해당 참가자 삭제
        esParticipantService.deleteById(participant.getId());

        // ! 5. 방 현재 인원 수 업데이트
        if (participant.getMember().getGender() == 'm') {
            videoRoom.setCurMCount(videoRoom.getCurMCount() - 1);
        } else if (participant.getMember().getGender() == 'f') {
            videoRoom.setCurWCount(videoRoom.getCurWCount() - 1);
        }

        // ! 6. 방장 일 경우 방장 교체 (가장 빨리 입장한 사람으로)
        if (videoRoom.getHost().getId().equals(memberId)) {
            // 참가자 중 가장 빠른 참여시간인 사람을 찾기
            VideoRoomParticipant earliestParticipant = videoRoom.getParticipants().stream()
                                                                .min(Comparator.comparing(
                                                                        VideoRoomParticipant::getJoinedAt))
                                                                .orElseThrow(() -> new VideoException(
                                                                        ErrorCode.PARTICIPANT_NOT_FOUND));
            // videoRoom host 를 해당 참가자로 수정
            videoRoom.setHost(earliestParticipant.getMember());
        }
        videoRoomRepository.save(videoRoom);
        return 0;
    }

    /**
     * 방 상세 정보 조회
     */
    @Override
    public VideoRoomDTO.DetailResponse getVideoRoomDetail(Long videoRoomId) {
        // ! 1. 방 찾기
        VideoRoom videoRoom = videoRoomRepository.findById(videoRoomId)
                                                 .orElseThrow(() -> new VideoException(ErrorCode.VIDEO_ROOM_NOT_FOUND));

        // ! 2. 방 참가자의 태그를 포함한 반환
        return VideoRoomDTO.DetailResponse.withDetails(videoRoom);
    }

    /**
     * 방 리스트 조회(최신순)
     */
    @Override
    public Map<String, Object> getVideoRoomList(Pageable pageable) {
        // ! 1. 대기방 리스트 최신순 조회
        Page<VideoRoom> videoRoomPage = videoRoomRepository.findByStatus(VideoRoomStatus.READY, pageable);

        List<VideoRoomDTO.BaseResponse> videoRoomResponses = videoRoomPage.stream()
                                                                          .map(BaseResponse::from)
                                                                          .collect(Collectors.toList());
        // ! 2. 응답용 리스트 데이터 가공
        Map<String, Object> response = new HashMap<>();
        response.put("videoRoomList", videoRoomResponses);
        response.put("page", Map.of(
                "totalElements", videoRoomPage.getTotalElements(),
                "totalPages", videoRoomPage.getTotalPages(),
                "currentPage", videoRoomPage.getNumber(),
                "pageSize", videoRoomPage.getSize()
        ));
        return response;
    }

    /**
     * 방 태그 필터를 통한 방 리스트 조회 (태그 일치가 많은 순)
     */
    @Override
    public Map<String, Object> getVideoRoomListWithFilter(Pageable pageable, List<Long> tagIds) {
        // ! 1. 대기방 리스트 조회
        List<VideoRoom> videoRooms = videoRoomRepository.findByStatus(VideoRoomStatus.READY);

        // 2. 필터를 포함하는 리스트 찾기 + 태그 일치 횟수 카운트
        Map<VideoRoomDTO.BaseResponse, Integer> mapVideoRoomResponses = new HashMap<>();
        for (VideoRoom videoRoom : videoRooms) {
            int count = 0;
            for (VideoRoomHobby videoRoomHobby : videoRoom.getVideoRoomHobby()) {
                if (tagIds.contains(videoRoomHobby.getTag().getId())) {
                    count++;
                }
            }
            for (VideoRoomPersonality videoRoomPersonality : videoRoom.getVideoRoomPersonality()) {
                if (tagIds.contains(videoRoomPersonality.getTag().getId())) {
                    count++;
                }
            }
            if (count > 0) {
                mapVideoRoomResponses.put(VideoRoomDTO.BaseResponse.from(videoRoom), count);
            }
        }

        // 3. 매칭 수 기준으로 내림차순 정렬
        List<Map.Entry<VideoRoomDTO.BaseResponse, Integer>> sortedEntries = mapVideoRoomResponses.entrySet()
                                                                                                 .stream()
                                                                                                 .sorted(Map.Entry.<VideoRoomDTO.BaseResponse, Integer>comparingByValue()
                                                                                                                  .reversed())
                                                                                                 .toList();

        // 4. 페이지화
        int totalElements = sortedEntries.size();
        int start = Math.min((int) pageable.getOffset(), totalElements);
        int end = Math.min(start + pageable.getPageSize(), totalElements);

        List<VideoRoomDTO.BaseResponse> videoRoomResponses = sortedEntries.subList(start, end)
                                                                          .stream()
                                                                          .map(Map.Entry::getKey)
                                                                          .collect(Collectors.toList());
        // 5. 응답 데이터 구성
        Page<VideoRoomDTO.BaseResponse> page = new PageImpl<>(videoRoomResponses, pageable, totalElements);

        Map<String, Object> response = new HashMap<>();
        response.put("videoRoomList", page.getContent());
        response.put("page", Map.of(
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "currentPage", page.getNumber(),
                "pageSize", page.getSize()
        ));

        return response;
    }

    @Override
    public Map<String, Object> getRecommendedVideoRoomList(int page, Long memberId) throws IOException {
        // ! 1. 멤버 찾기
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        TypeProfiles typeProfiles = typeProfilesRepository.findByMemberId(memberId).orElseThrow(
                () -> new MemberException(ErrorCode.TYPE_PROFILES_NOT_FOUND));

        // ! 2. page, size, memberId 를 elastic search 에 보내기
        List<Long> recommendRoomIds = esParticipantService.recommendRoom(page, Request.toRecommendRequest(member,

                                                                                                          typeProfiles));
        // ! videoRoom상태 체크
        List<VideoRoom> videoRooms = checkVideoRoomStatus(recommendRoomIds, page);

        // ! 3. 미팅룸 id를 기반으로 미팅룸 리스트 형태 만들기
        List<VideoRoomDTO.BaseResponse> videoRoomResponses = new ArrayList<>();

        for (VideoRoom videoRoom : videoRooms) {
            videoRoomResponses.add(VideoRoomDTO.DetailResponse.from(videoRoom));
        }

        // ! 4. 응답용 리스트 데이터 가공
        Map<String, Object> response = new HashMap<>();
        response.put("videoRoomList", videoRoomResponses);
        response.put("page", Map.of(
                "totalElements", recommendRoomIds.size(),
                "totalPages", recommendRoomIds.size() / 5,
                "currentPage", page,
                "pageSize", 5
        ));

        return response;
    }

    // ! 비디오룸의 상태가 READY인 방만 filtering하여 추천 리스트를 만들어주는 메소드
    private List<VideoRoom> checkVideoRoomStatus(List<Long> recommendRoomIds, int page) {
        int recommendListSize = recommendRoomIds.size();
        List<VideoRoom> videoRooms = new ArrayList<>();
        int startIndex = page * 5;

        // 요청한 페이지 보다 List의 양이 적을 경우
        if (startIndex >= recommendListSize) {
            throw new VideoException(ErrorCode.INVALID_PAGE_REQUEST);
        } else {
            for (int i = page * 5; i < recommendListSize; i++) {
                VideoRoom videoRoom = videoRoomRepository.findById(recommendRoomIds.get(i)).orElseThrow(
                        () -> new VideoException(ErrorCode.VIDEO_ROOM_NOT_FOUND));

                if (videoRoom.getStatus() == VideoRoomStatus.READY) {
                    videoRooms.add(videoRoom);
                    if (videoRooms.size() == 5) {
                        break;
                    }
                }
            }
        }
        return videoRooms;
    }

    @Override
    public Long getRecentVideoRoom(Long hostId) {
        // ! 1. 호스트 찾기
        Member host = memberRepository.findById(hostId)
                                      .orElseThrow(() -> new VideoException(ErrorCode.HOST_NOT_FOUND));

        // 2. 최근 생성된 비디오 룸 조회
        VideoRoom videoRoom = videoRoomRepository.findTopByHostOrderByCreatedAtDesc(host).orElseThrow(
                () -> new VideoException((ErrorCode.VIDEO_ROOM_NOT_FOUND)));

        return videoRoom.getVideoRoomId();
    }

    /**
     * 미팅룸 취미 태그 찾아오기
     */
    public List<VideoRoomHobby> toHobbyList(VideoRoomDTO.Request videoRoomRequest, VideoRoom videoRoom) {
        List<VideoRoomHobby> videoRoomHobbies = new ArrayList<>();
        if (videoRoomRequest.getVideoRoomHobbies() != null) {
            for (Long tagId : videoRoomRequest.getVideoRoomHobbies()) {
                videoRoomHobbies.add(VideoRoomHobby.builder()
                                                   .tag(tagRepository.findById(tagId)
                                                                     .orElseThrow(() -> new TagException(
                                                                             ErrorCode.TAG_NOT_FOUND)))
                                                   .videoRoom(videoRoom)
                                                   .build());
            }
        }
        return videoRoomHobbies;
    }

    /**
     * 미팅룸 성격 태그 찾아오기
     */
    public List<VideoRoomPersonality> toPersonalityList(VideoRoomDTO.Request videoRoomRequest, VideoRoom videoRoom) {
        List<VideoRoomPersonality> videoRoomPersonalities = new ArrayList<>();
        if (videoRoomRequest.getVideoRoomPersonalities() != null) {
            for (Long tagId : videoRoomRequest.getVideoRoomPersonalities()) {
                videoRoomPersonalities.add(VideoRoomPersonality.builder()
                                                               .tag(tagRepository.findById(tagId)
                                                                                 .orElseThrow(() -> new TagException(
                                                                                         ErrorCode.TAG_NOT_FOUND)))
                                                               .videoRoom(videoRoom)
                                                               .build());
            }
        }
        return videoRoomPersonalities;
    }
}