package com.cupid.qufit.domain.member.service;

import static com.cupid.qufit.domain.member.util.MemberBirthDateUtil.convertToLocalDate;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO;
import com.cupid.qufit.domain.member.repository.mappingTags.MemberHobbyRepository;
import com.cupid.qufit.domain.member.repository.mappingTags.MemberPersonalityRepository;
import com.cupid.qufit.domain.member.repository.mappingTags.TypeHobbyRepository;
import com.cupid.qufit.domain.member.repository.mappingTags.TypeMBTIRepository;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.member.repository.profiles.TypeProfilesRepository;
import com.cupid.qufit.domain.member.repository.tag.LocationRepository;
import com.cupid.qufit.domain.member.repository.tag.TagRepository;
import com.cupid.qufit.entity.Location;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberHobby;
import com.cupid.qufit.entity.MemberPersonality;
import com.cupid.qufit.entity.MemberStatus;
import com.cupid.qufit.entity.Tag;
import com.cupid.qufit.entity.TagCateg;
import com.cupid.qufit.entity.TypeHobby;
import com.cupid.qufit.entity.TypeMBTI;
import com.cupid.qufit.entity.TypePersonality;
import com.cupid.qufit.entity.TypeProfiles;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.exception.exceptionType.TagException;
//import com.cupid.qufit.global.redis.service.RedisRefreshTokenService;
import com.cupid.qufit.global.security.util.JWTUtil;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService {

    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final TypeProfilesRepository typeProfilesRepository;
    private final JWTUtil jwtUtil;
//    private final RedisRefreshTokenService redisRefreshTokenService;
    private final MemberHobbyRepository memberHobbyRepository;
    private final MemberPersonalityRepository memberPersonalityRepository;
    private final TypeMBTIRepository typeMBTIRepository;
    private final TypeHobbyRepository typeHobbyRepository;
    private final TypeHobbyRepository typePersonalityRepository;

    // openai 관련 추가
    private final RestTemplate restTemplate = new RestTemplate();

//    @Value("${openai.api.key}")
    private String OPENAI_API_KEY ="aaa";
    /*
     * * 회원 프로필 (지역, mbti, 취미, 성격) 저장
     * */
    @Override
    public void saveMemberProfiles(Member member, MemberInfoDTO.Request requestDTO) {
        // location 저장
        this.saveMemberLocation(member, requestDTO.getLocationId());

        // mbti 저장
        this.saveMemberMBTI(member, requestDTO.getMemberMBTITag());

        // hobby 저장
        List<String> memberHobbyTagNames = requestDTO.getMemberHobbyTags();
        this.saveMemberHobbies(member, memberHobbyTagNames);

        // Personality 저장
        List<String> memberPersonalityTagNames = requestDTO.getMemberPersonalityTags();
        this.saveMemberPersonalities(member, memberPersonalityTagNames);

    }

    /*
     * * 이상형 프로필 생성, 나이차 저장
     * */
    @Override
    public TypeProfiles createTypeProfiles(Member member, MemberInfoDTO.Request requestDTO) {
        return TypeProfiles.builder()
                .member(member)
                .typeAgeMax(requestDTO.getTypeAgeMax())
                .typeAgeMin(requestDTO.getTypeAgeMin())
                .build();
    }

    /*
     * * 이상형 프로필 (지역, mbti, 취미, 성격) 저장
     * */
    @Override
    public void saveTypeProfilesInfo(TypeProfiles typeProfiles, MemberInfoDTO.Request requestDTO) {
        // mbti 저장
        List<String> typeMBTINames = requestDTO.getTypeMBTITags();
        this.saveTypeMBTI(typeProfiles, typeMBTINames);

        // hobby 저장
        List<String> typeHobbyNames = requestDTO.getTypeHobbyTags();
        this.saveTypeHobbies(typeProfiles, typeHobbyNames);

        // Personality 저장
        List<String> typePersonalityNames = requestDTO.getTypePersonalityTags();
        this.saveTypePersonalities(typeProfiles, typePersonalityNames);

    }

    /*
     * * 로그인 성공 시 jwt 발급
     *
     * @param : 로그인 성공 처리된 MemberDetails
     * - 카카오 로그인된 회원 email이 db에 존재하며 승인된 회원일 경우 로그인 처리
     * */
    @Override
    public Map<String, MemberSigninDTO.Response> signIn(MemberDetails memberDetails) {
        String accessToken = jwtUtil.generateToken(memberDetails.getClaims(), "access");
//        String refreshToken = jwtUtil.generateToken(memberDetails.getClaims(), "refresh");
//        redisRefreshTokenService.saveRedisData(memberDetails.getId(), refreshToken,
//                accessToken); // refreshToken redis에 저장

        Member member = memberRepository.findById(memberDetails.getId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        MemberSigninDTO.Response responseDTO = MemberSigninDTO.Response.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .gender(member.getGender())
                .build();

        Map<String, MemberSigninDTO.Response> result = new HashMap<>();
        result.put(accessToken, responseDTO);
        return result;
    }

    /*
     * * 회원 정보 조회
     * */
    @Override
    public MemberInfoDTO.Response getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        TypeProfiles typeProfiles = typeProfilesRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(
                        ErrorCode.TYPE_PROFILES_NOT_FOUND));
        return MemberInfoDTO.Response.of(member, typeProfiles);
    }

    /*
     * * 회원 정보 수정
     *
     * @param 회원가입 요청 DTO 와 동일
     *
     * */
    @Override
    public MemberInfoDTO.Response updateMemberInfo(MemberInfoDTO.Request request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 회원 프로필 수정
        member.updateNickname(request.getNickname());
        member.updateBirthDate(convertToLocalDate(request.getBirthYear()));
        member.updateGender(request.getGender().charAt(0));
        member.updateBio(request.getBio());

        // 회원 프로필 정보(지역, mbti, 취미, 성격) 수정
        saveMemberProfiles(member, request);

        // 이상형 프로필 수정
        TypeProfiles typeProfiles = typeProfilesRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(
                        ErrorCode.TYPE_PROFILES_NOT_FOUND));

        typeProfiles.updateTyeAgeMax(request.getTypeAgeMax());
        typeProfiles.updateTypeAgeMin(request.getTypeAgeMin());

        // 이상형 프로필 정보(mbti, 취미, 성격) 수정
        saveTypeProfilesInfo(typeProfiles, request);

        Member updatedMember = memberRepository.save(member);
        TypeProfiles updatedType = typeProfilesRepository.save(typeProfiles);

        return MemberInfoDTO.Response.of(updatedMember, updatedType);
    }

    /*
     * * 회원 탈퇴 처리
     * */
    @Override
    public Member deleteService(Long currentMemberId) {
        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원 상태 확인
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_WITHDRAWN);
        }
        member.updateStatus(MemberStatus.WITHDRAWN);

        return memberRepository.save(member);
    }

    /**
     * 프로필 이미지 생성 및 저장
     */
    @Override
    public void generateImage(MemberDetails memberDetails) throws Exception {
        Member member = memberRepository.findById(memberDetails.getId()).orElseThrow();
        StringBuilder prompt = new StringBuilder();
        int cnt = 0;
        for (MemberPersonality memberPersonality : member.getMemberPersonalities()) {
            prompt.append(memberPersonality.getTag().getTagName());
            if (++cnt != member.getMemberPersonalities().size()) {
                prompt.append(", ");
            }
        }
        prompt.append(" 성격을 종합한 모습을 지닌 한마리의 귀여운 픽셀 동물 그림 그려줘");
        String apiEndpoint = "https://api.openai.com/v1/images/generations";

        // Prepare request body
        String requestBody = "{"
                + "\"model\": \"dall-e-3\","
                + "\"prompt\": \"" + prompt + "\","
                + "\"n\": 1,"
                + "\"size\": \"1024x1024\""
                + "}";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);

        // Create request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, requestEntity, String.class);

        // Handle response
        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject responseJson = new JSONObject(response.getBody());
            String imageUrl = responseJson.getJSONArray("data").getJSONObject(0).getString("url");

            // 회원 정보에 저장
            member.setProfileImage(imageUrl);
            memberRepository.save(member);
        } else {
            throw new Exception("Failed to generate image, response code: " + response.getStatusCode());
        }
    }

    // ! 아래는 회원 부가정보 (지역, mbti, 취미, 성격) 저장
    private void saveMemberLocation(Member member, Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new TagException(ErrorCode.LOCATION_NOT_FOUND));
        member.updateLocation(location);
    }

    private void saveMemberMBTI(Member member, String tagName) {
        if (tagName == null) {
            tagName = "none";
        }

        Tag mbti = findByTagName(tagName);
        if (mbti.getTagCategory() == TagCateg.MBTI) {
            member.updateMBTI(mbti);
        }
    }

    private void saveMemberHobbies(Member member, List<String> memberHobbyTagNames) {
        if (!member.getMemberHobbies().isEmpty()) {
            member.getMemberHobbies().clear();
            memberHobbyRepository.deleteAllByMemberId(member.getId());
        }

        if (!memberHobbyTagNames.isEmpty()) {
            memberHobbyTagNames.forEach(name -> {
                Tag tag = findByTagName(name);
                if (tag.getTagCategory() == TagCateg.HOBBY) {
                    MemberHobby memberHobby = MemberHobby.builder()
                            .tag(findByTagName(name))
                            .build();
                    member.addMemberHobbies(memberHobby);
                    log.info(memberHobby.getTag().getTagName());
                }
            });
        }
    }

    private void saveMemberPersonalities(Member member, List<String> memberPersonalityTagNames) {
        if (!member.getMemberPersonalities().isEmpty()) {
            member.getMemberPersonalities().clear();
            memberPersonalityRepository.deleteAllByMemberId(member.getId());
        }

        if (!memberPersonalityTagNames.isEmpty()) {
            memberPersonalityTagNames.forEach(name -> {
                Tag tag = findByTagName(name);
                if (tag.getTagCategory() == TagCateg.PERSONALITY) {
                    MemberPersonality memberPersonality = MemberPersonality.builder()
                            .tag(tag)
                            .build();
                    member.addMemberPersonalities(memberPersonality);
                }
            });
        }
    }

    // ! 아래는 이상형 부가정보 (mbti, 취미, 성격) 저장
    private void saveTypeMBTI(TypeProfiles typeProfiles, List<String> typeMBTINames) {
        if (!typeProfiles.getTypeMBTIs().isEmpty()) {
            typeProfiles.getTypeMBTIs().clear();
            typeMBTIRepository.deleteAllByTypeProfilesId(typeProfiles.getId());
        }

        if (typeMBTINames == null || typeMBTINames.isEmpty()) {
            typeMBTINames = new ArrayList<>(Collections.singleton("none"));
        }
        typeMBTINames.forEach(name -> {
            Tag tag = findByTagName(name);
            if (tag.getTagCategory() == TagCateg.MBTI) {
                TypeMBTI typeMBTI = TypeMBTI.builder()
                        .tag(tag)
                        .build();
                typeProfiles.addtypeMBTIs(typeMBTI);
            }
        });
    }

    private void saveTypeHobbies(TypeProfiles typeProfiles, List<String> typeHobbyNames) {
        if (!typeProfiles.getTypeHobbies().isEmpty()) {
            typeProfiles.getTypeHobbies().clear();
            typeHobbyRepository.deleteAllByTypeProfilesId(typeProfiles.getId());
        }

        if (!typeHobbyNames.isEmpty()) {
            typeHobbyNames.forEach(name -> {
                Tag tag = findByTagName(name);
                if (tag.getTagCategory() == TagCateg.HOBBY) {
                    TypeHobby typeHobby = TypeHobby.builder()
                            .tag(tag)
                            .build();
                    typeProfiles.addTypeHobbies(typeHobby);
                }
            });
        }
    }

    private void saveTypePersonalities(TypeProfiles typeProfiles, List<String> typePersonalityNames) {
        if (!typeProfiles.getTypePersonalities().isEmpty()) {
            typeProfiles.getTypePersonalities().clear();
            typePersonalityRepository.deleteAllByTypeProfilesId(typeProfiles.getId());
        }

        if (!typePersonalityNames.isEmpty()) {
            typePersonalityNames.forEach(name -> {
                Tag tag = findByTagName(name);
                if (tag.getTagCategory() == TagCateg.PERSONALITY) {
                    TypePersonality typePersonality = TypePersonality.builder()
                            .tag(tag)
                            .build();
                    typeProfiles.addTypePersonalities(typePersonality);
                }
            });
        }
    }

    /*
     * * 태그이름으로 태그를 찾는 메소드
     * */
    private Tag findByTagName(String tagName) {
        return tagRepository.findByTagName(tagName)
                .orElseThrow(() -> new TagException(ErrorCode.TAG_NOT_FOUND));
    }
}
