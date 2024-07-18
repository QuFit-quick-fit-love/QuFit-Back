package com.cupid.qufit.domain.member.controller;

import com.cupid.qufit.domain.member.dto.PrincipalDetails;
import com.cupid.qufit.domain.member.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /*
    * * 카카오 소셜 로그인
    *
    * TODO : 로그인 처리 (JWT 발급)
    *
    * @param accessToken 카카오에서 발급받은 accessToken
    * */

    @GetMapping("/login")
    public ResponseEntity<?> KakaoLogin(String accessToken){
        PrincipalDetails principalDetails = authService.kakaoLogin(accessToken);

        Map<String, Object> claims = principalDetails.getClaims();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
