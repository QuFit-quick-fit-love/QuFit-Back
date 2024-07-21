package com.cupid.qufit.domain.member.controller;

import com.cupid.qufit.domain.member.dto.MemberSignupDTO;
import com.cupid.qufit.domain.member.dto.PrincipalDetails;
import com.cupid.qufit.domain.member.service.AuthService;
import com.cupid.qufit.global.exception.exceptionType.MemberException;

import jakarta.validation.Valid;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Log4j2
public class AuthController {

    private final AuthService authService;

    /*
    * * 카카오 소셜 로그인
    *
    * TODO : 로그인 처리 (JWT 발급)
    *
    * @param : accessToken 카카오에서 발급받은 accessToken
    * */

    @GetMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam("accessToken") String accessToken){
        PrincipalDetails principalDetails = authService.kakaoLogin(accessToken);

        Map<String, Object> claims = principalDetails.getClaims();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    * * 부가정보 입력 후 회원가입 처리
    *
    * @ param : accessToken 카카오에서 발급받은 accessToken
    * @ body : 회원이 입력한 부가 정보
    * */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam("accessToken") String accessToken, @Valid @RequestBody MemberSignupDTO.request requestDTO){
        log.info("---------------회원가입 시도-----------");
        try {
        	MemberSignupDTO.response memberSignupResponseDTO = authService.signup(accessToken, requestDTO);
        } catch (Exception e) {
            log.error("회원 가입 중 오류 발생", e);
            throw new MemberException(ErrorCode.SIGNUP_FAILURE);
        }
        
        return new ResponseEntity<>(memberSignupResponseDTO, HttpStatus.OK);
    }
}
