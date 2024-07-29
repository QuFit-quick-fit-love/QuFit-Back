package com.cupid.qufit.domain.member.controller;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.dto.MemberSigninDTO.Response;
import com.cupid.qufit.domain.member.service.AuthService;
import com.cupid.qufit.domain.member.service.MemberService;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qufit/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
@Log4j2
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    /*
     * * 카카오 소셜 로그인
     *
     * @param : accessToken 카카오에서 발급받은 accessToken
     * */
    @GetMapping(path = "/login", headers = "accessToken")
    public ResponseEntity<?> kakaoLogin(@RequestHeader("accessToken") String accessToken) {
        MemberDetails memberDetails = authService.kakaoLogin(accessToken);

        if (memberDetails != null) {
            // 유저 정보와 토큰 생성
            Map<String, Response> result = memberService.signIn(memberDetails);
            String token = result.keySet().iterator().next();
            log.info("[login token] : " + token);

            // 헤더에 토큰 저장
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            return new ResponseEntity<>(result.get(token), headers, HttpStatus.OK);
        }
        throw new MemberException(ErrorCode.MEMBER_DEFAULT_ERROR);
    }

    @PostMapping(path = "/signup", headers = "accessToken")
    public ResponseEntity<?> signup(@RequestHeader("accessToken") String accessToken,
                                    @Valid @RequestBody MemberInfoDTO.Request requestDTO) {
        log.info("---------------회원가입 시도-----------");

        MemberInfoDTO.Response responseDTO;
        try {
            responseDTO = authService.signup(accessToken, requestDTO);
        } catch (Exception e) {
            log.error("회원 가입 중 오류 발생", e);
            throw new MemberException(ErrorCode.SIGNUP_FAILURE);
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkUniqueNickname(@RequestParam("nickname") String nickname){
        log.info("---------------닉네임 중복검사-----------");
        Boolean isNicknameDuplication = authService.isNicknameDuplication(nickname);
        if(!isNicknameDuplication) {
            return new ResponseEntity<>(nickname + " 은(는) 사용가능한 닉네임입니다.", HttpStatus.OK);
        }
        return new ResponseEntity<>(nickname + " 은(는) 중복된 닉네임입니다.", HttpStatus.BAD_REQUEST);
    }
}
