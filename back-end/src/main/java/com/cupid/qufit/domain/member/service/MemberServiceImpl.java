package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO.response;
import com.cupid.qufit.domain.member.dto.MemberSignupDTO;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.member.repository.tag.LocationRepository;
import com.cupid.qufit.domain.member.repository.tag.TagRepository;
import com.cupid.qufit.entity.Location;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberHobby;
import com.cupid.qufit.entity.MemberPersonality;
import com.cupid.qufit.entity.Tag;
import com.cupid.qufit.entity.TypeHobby;
import com.cupid.qufit.entity.TypeMBTI;
import com.cupid.qufit.entity.TypePersonality;
import com.cupid.qufit.entity.TypeProfiles;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.exception.exceptionType.TagException;
import com.cupid.qufit.global.redis.service.RedisRefreshTokenService;
import com.cupid.qufit.global.security.util.JWTUtil;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService {

    private final LocationRepository locationRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final JWTUtil jwtUtil;
    private final RedisRefreshTokenService redisRefreshTokenService;

    /*
     * * 회원 프로필 (지역, mbti, 취미, 성격) 저장
     * */
    @Override
    public void saveMemberProfiles(Member member, MemberSignupDTO.request requestDTO) {
        // location 저장
        this.saveMemberLocation(member, requestDTO.getMemberLocationId());

        // mbti 저장
        this.saveMemberMBTI(member, requestDTO.getMemberMBTITagId());

        // hobby 저장
        List<Long> memberHobbyIds = requestDTO.getMemberHobbyTagIds();
        this.saveMemberHobbies(member, memberHobbyIds);

        // Personality 저장
        List<Long> memberPersonalityIds = requestDTO.getMemberHobbyTagIds();
        this.saveMemberPersonalities(member, memberPersonalityIds);

    }

    /*
     * * 이상형 프로필 생성, 나이차 저장
     * */
    @Override
    public TypeProfiles createTypeProfiles(Member member, MemberSignupDTO.request requestDTO) {
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
    public void saveTypeProfilesInfo(TypeProfiles typeProfiles, MemberSignupDTO.request requestDTO) {
        // mbti 저장
        List<Long> typeMBTIIds = requestDTO.getTypeMBTITagIds();
        this.saveTypeMBTI(typeProfiles, typeMBTIIds);

        // hobby 저장
        List<Long> typeHobbyIds = requestDTO.getMemberHobbyTagIds();
        this.saveTypeHobbies(typeProfiles, typeHobbyIds);

        // Personality 저장
        List<Long> typePersonalityIds = requestDTO.getMemberHobbyTagIds();
        this.saveTypePersonalities(typeProfiles, typePersonalityIds);

    }

    /*
     * * 로그인 성공 시 jwt 발급
     *
     * @param : 로그인 성공 처리된 MemberDetails
     * - 카카오 로그인된 회원 email이 db에 존재하며 승인된 회원일 경우 로그인 처리
     * */
    @Override
    public Map<String, response> signIn(MemberDetails memberDetails) {
        String accessToken = jwtUtil.generateToken(memberDetails.getClaims(), "access");
        String refreshToken = jwtUtil.generateToken(memberDetails.getClaims(), "refresh");
        redisRefreshTokenService.saveRedisData(memberDetails.getId(), refreshToken, accessToken); // refreshToken redis에 저장

        Member member = memberRepository.findById(memberDetails.getId())
                                        .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        MemberSigninDTO.response responseDTO = MemberSigninDTO.response.builder()
                                       .email(member.getEmail())
                                       .nickname(member.getNickname())
                                       .profileImage(member.getProfileImage())
                                       .gender(member.getGender())
                                       .build();

        Map<String, response> result = new HashMap<>();
        result.put(accessToken, responseDTO);
        return result;
    }

    private void saveMemberLocation(Member member, Long locationId) {
        Location location = locationRepository.findById(locationId)
                                              .orElseThrow(() -> new TagException(ErrorCode.LOCATION_NOT_FOUND));
        member.updateLocation(location);
    }

    private void saveMemberMBTI(Member member, Long tagId) {
        Tag mbti = tagRepository.findById(tagId)
                                .orElseThrow(() -> new TagException(ErrorCode.TAG_NOT_FOUND));
        member.updateMBTI(mbti);
    }

    private void saveMemberHobbies(Member member, List<Long> memberHobbyIds) {
        if (!memberHobbyIds.isEmpty()) {
            memberHobbyIds.forEach(tagId -> {
                MemberHobby memberHobby = MemberHobby.builder()
                                                     .tag(tagRepository.findById(tagId)
                                                                       .orElseThrow(() -> new TagException(
                                                                               ErrorCode.TAG_NOT_FOUND)))
                                                     .build();
                member.addMemberHobbies(memberHobby);
            });
        }
    }

    private void saveMemberPersonalities(Member member, List<Long> memberPersonalityIds) {
        if (!memberPersonalityIds.isEmpty()) {
            memberPersonalityIds.forEach(tagId -> {
                MemberPersonality memberPersonality = MemberPersonality.builder()
                                                                       .tag(tagRepository.findById(tagId).orElseThrow(
                                                                               () -> new TagException(
                                                                                       ErrorCode.TAG_NOT_FOUND)))
                                                                       .build();
                member.addMemberPersonalities(memberPersonality);
            });
        }
    }

    private void saveTypeMBTI(TypeProfiles typeProfiles, List<Long> typeMBTIIds) {
        if (!typeMBTIIds.isEmpty()) {
            typeMBTIIds.forEach(tagId -> {
                TypeMBTI typeMBTI = TypeMBTI.builder()
                                            .tag(tagRepository.findById(tagId).orElseThrow(
                                                    () -> new TagException(ErrorCode.TAG_NOT_FOUND)))
                                            .build();
                typeProfiles.addtypeMBTIs(typeMBTI);
            });
        }
    }

    private void saveTypeHobbies(TypeProfiles typeProfiles, List<Long> typeHobbyIds) {
        if (!typeHobbyIds.isEmpty()) {
            typeHobbyIds.forEach(tagId -> {
                TypeHobby typeHobby = TypeHobby.builder()
                                               .tag(tagRepository.findById(tagId).orElseThrow(
                                                       () -> new TagException(ErrorCode.TAG_NOT_FOUND)))
                                               .build();
                typeProfiles.addTypeHobbies(typeHobby);
            });
        }
    }

    private void saveTypePersonalities(TypeProfiles typeProfiles, List<Long> typePersonalityIds) {
        if (!typePersonalityIds.isEmpty()) {
            typePersonalityIds.forEach(tagId -> {
                TypePersonality typePersonality = TypePersonality.builder()
                                                                 .tag(tagRepository.findById(tagId).orElseThrow(
                                                                         () -> new TagException(
                                                                                 ErrorCode.TAG_NOT_FOUND)))
                                                                 .build();
                typeProfiles.addTypePersonalities(typePersonality);
            });
        }
    }
}
