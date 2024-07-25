package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.MemberSignupDTO;
import com.cupid.qufit.domain.member.dto.MemberSignupDTO.request;
import com.cupid.qufit.domain.member.dto.MemberSignupDTO.response;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.member.repository.profiles.TypeProfilesRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.entity.MemberStatus;
import com.cupid.qufit.entity.TypeProfiles;
import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;


@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final TypeProfilesRepository typeProfilesRepository;
    private final MemberService memberService;

    @Value("${kakao.user.info.url}")
    private String kakaoGetUserInfoUrl;

    /*
     * * 카카오 계정 이메일과 서버 DB 회원 정보와 비교해 확인시 로그인 처리
     *
     * * DB에 존재하는 회원일 경우 1) 승인회원 -> 로그인 2) 대기회원 -> 예외 발생
     * * DB에 존재하지 않는 회원일 경우 -> 예외 발생
     */
    @Override
    public MemberDetails kakaoLogin(String accessToken) {
        // accessToken을 이용해서 사용자 정보 가져오기
        LinkedHashMap<String, LinkedHashMap> kakaoAccount = getKakaoAccountFromKakao(accessToken);
        String email = String.valueOf(kakaoAccount.get("email"));
        log.info("----------------------------------");
        log.info("email : " + email);
        // 기존 DB에 회원정보 조회
        Optional<Member> resultMember = memberRepository.findByEmail(email);

        if (resultMember.isPresent()) {
            // 존재하면 대기/승인 상태 확인
            if (resultMember.get().getStatus() == MemberStatus.APPROVED) {
                // 승인 회원이면 로그인 처리
                MemberDetails existedDTO = entityToMemberDetails(resultMember.get());
                return existedDTO;
            } else if (resultMember.get().getStatus() == MemberStatus.PENDING){
            // 대기중이면 대기중 error
                throw new CustomException(ErrorCode.ACCEPT_PENDING_USER);
            }
        } else if (!resultMember.isPresent()){
        // 존재하지 않으면 회원가입 error
            throw new CustomException(ErrorCode.SIGNUP_REQUIRED);
        }
        return null;
    }

    /*
     * * 입력받은 회원 부가정보로 회원가입 처리
     */
    @Override
    public response signup(String accessToken, request requestDTO) {
        // accessToken으로 카카오 회원 정보 조회
        LinkedHashMap<String, LinkedHashMap> kakaoAccount = getKakaoAccountFromKakao(accessToken);

        Map<String, String> kakaoProfile = kakaoAccount.get("profile");
        log.info("----------------------------------");
        log.info("kakaoAccount : " + kakaoAccount);

        String email = String.valueOf(kakaoAccount.get("email"));

        //  중복회원 검증
        Optional<Member> resultMember = memberRepository.findByEmail(email);
        log.info("중복회원 검증 : " + resultMember.isPresent());
        if (resultMember.isPresent()) throw new MemberException(ErrorCode.USERNAME_ALREADY_EXISTS);

        String profileURL = "";
        if (kakaoProfile.get("is_default_image") == "false") {
            profileURL = String.valueOf(kakaoAccount.get("profile_img_url"));
        }
//        String nickname = String.valueOf(kakaoAccount.get("nickname"));

        // 회원 정보 저장
        Member newMember = makeNewMemberEntity(email, profileURL, requestDTO);

        // 회원 프로필 mbti, 취미, 성격 저장
        memberService.saveMemberProfiles(newMember, requestDTO);
        Member saveMember = memberRepository.save(newMember);

        // 이상형 프로필 생성
        TypeProfiles typeProfiles = memberService.createTypeProfiles(saveMember, requestDTO);
        // 이상형 프로필 저장
        memberService.saveTypeProfilesInfo(typeProfiles, requestDTO);
        typeProfilesRepository.save(typeProfiles);

        // 응답 DTO 반환 (테스트용)
        return response.builder()
                .email(saveMember.getEmail())
                .nickname(saveMember.getNickname())
                .memberLocation(saveMember.getLocation())
                .birthDate(saveMember.getBirthDate())
                .gender(saveMember.getGender())
                .bio(saveMember.getBio())
                .memberMBTITag(saveMember.getMBTI())
                .memberHobbyTags(saveMember.getMemberHobbies())
                .memberPersonalityTags(saveMember.getMemberPersonalities())
                .typeAgeMax(typeProfiles.getTypeAgeMax())
                .typeAgeMin(typeProfiles.getTypeAgeMin())
                .typeHobbyTags(typeProfiles.getTypeHobbies())
                .typePersonalityTags(typeProfiles.getTypePersonalities())
                .build();
    }

    /*
     * * accessToken으로 카카오 회원정보 조회
     *
     * @Return 회원의 카카오 이메일 계정, 프로필사진url, 닉네임
     * */
    private LinkedHashMap<String, LinkedHashMap> getKakaoAccountFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserInfoUrl).build();

        ResponseEntity<LinkedHashMap> responseKakaoUserInfo =
                restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        LinkedHashMap<String, LinkedHashMap> bodyMap = responseKakaoUserInfo.getBody();

        LinkedHashMap<String, LinkedHashMap> kakaoAccount = bodyMap.get("kakao_account");
        log.info("// kakaoAccount : " + kakaoAccount);
        return kakaoAccount;
    }

    /*
     * * 카카오 회원 계정 정보와 부가정보로 Member 엔티티 생성
     *
     * TODO : 랜덤 생성된 비밀번호 암호화해서 저장
     * @param : 카카오 회원 계정 이메일, 프로필사진 url, 부가정보 DTO
     * */
    private Member makeNewMemberEntity(String email, String profileURL,
                                       MemberSignupDTO.request requestDTO) {
        String tmpPW = makeTmpPW();

        return Member.builder()
                     .location(null)
                     .role(MemberRole.USER)
                     .email(email)
                     .password(tmpPW)
                     .nickname(requestDTO.getNickname())
                     .birthDate(LocalDate.parse(requestDTO.getBirthYear() + "0101", DateTimeFormatter.ofPattern("yyyyMMdd")))
                     .gender(requestDTO.getGender())
                     .bio(requestDTO.getBio())
                     .profileImage(profileURL)
                     .MBTI(null)
                     .build();
    }

    private String makeTmpPW() {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) * 65));
        }
        return buffer.toString();
    }
}
