package com.cupid.qufit.domain.member.service;

import com.cupid.qufit.domain.member.dto.PrincipalDetails;
import com.cupid.qufit.domain.member.repository.MemberRepository;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberStatus;
import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;
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

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final String KAKAO_GET_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    /*
     * * 카카오 계정 이메일과 서버 DB 회원 정보와 비교해 확인시 로그인 처리
     *
     * * DB에 존재하는 회원일 경우 1) 승인회원 -> 로그인 2) 대기회원 -> 예외 발생
     * * DB에 존재하지 않는 회원일 경우 -> 예외 발생
     */
    @Override
    public PrincipalDetails kakaoLogin(String accessToken) {
        // accessToken을 이용해서 사용자 정보 가져오기
        String email = getEmailFromKakaoAccessToken(accessToken);
        log.info("----------------------------------");
        log.info("email : " +email);
        // 기존 DB에 회원정보 조회
        Optional<Member> resultMember = memberRepository.findByEmail(email);

        if(resultMember.isPresent()){
            // 존재하면 대기/승인 상태 확인
            if(resultMember.get().getStatus() == MemberStatus.APPROVED){
                // 승인 회원이면 로그인 처리
                PrincipalDetails existedDTO = entityToPrincipalDetails(resultMember.get());
                return existedDTO;
            }
            else if(resultMember.get().getStatus() == MemberStatus.PENDING){
                // 대기중이면 대기중 error
                throw new CustomException(ErrorCode.ACCEPT_PENDING_USER);
            }
        }
        else if(!resultMember.isPresent()){
            // 존재하지 않으면 회원가입 error
            throw new CustomException(ErrorCode.SIGNUP_REQUIRED);
        }
        return null;
    }

    /*
    * * accessToken으로 회원 카카오 계정 조회
    *
    * @Return 회원의 카카오 이메일 계정
    */

    private String getEmailFromKakaoAccessToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","Bearer " + accessToken);
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_GET_USER_INFO_URL).build();

        ResponseEntity<LinkedHashMap> responseKakaoUserInfo =
                restTemplate.exchange(uriComponentsBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        LinkedHashMap<String, LinkedHashMap> bodyMap = responseKakaoUserInfo.getBody();

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");

        String kakaoEmail = kakaoAccount.get("email");
        log.info("kakaoEmail : " + kakaoEmail);

        return kakaoEmail;
    }
}
