package com.cupid.qufit.domain.member.controller;

import com.cupid.qufit.domain.member.dto.LoginByIdRequest;
import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.repository.profiles.MemberRepository;
import com.cupid.qufit.domain.member.service.AuthService;
import com.cupid.qufit.domain.member.service.MemberService;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.global.common.response.FieldValidationExceptionResponse;
import com.cupid.qufit.global.exception.ErrorResponse;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.security.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JWTUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<?> loginById(@RequestBody LoginByIdRequest loginRequest, HttpServletRequest request) {
        // 1. 요청으로 받은 id에 해당하는 사용자 정보 조회
        // 1. 요청으로 받은 id에 해당하는 사용자 정보 조회
        Member member = memberRepository.findById(loginRequest.getId())
                                        .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 사용자 정보를 바탕으로 MemberDetails 생성
        MemberDetails memberDetails = MemberDetails.builder()
                                                   .id(member.getId())
                                                   .email(member.getEmail())
                                                   .pw(member.getPassword())
                                                   .role(member.getRole())
                                                   .build();

        // 3. (추가) JWT 발급: jwtUtil.generateToken 메서드를 사용하여 토큰 생성
        String accessToken = jwtUtil.generateToken(memberDetails.getClaims(), "access");

        // 4. 클라이언트에 accessToken 반환 (예: ResponseBody에 담거나 헤더에 추가)
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        return ResponseEntity.ok(result);
    }
    /*
     * * 카카오 소셜 로그인
     *
     * @param : accessToken 카카오에서 발급받은 accessToken
     * */
//    @GetMapping(path = "/login", headers = "accessToken")
//    @Operation(summary = "카카오 소셜 로그인", description = "카카오 소셜 로그인을 시도한다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "로그인 성공"),
//            @ApiResponse(responseCode = "401", description = "ACCEPT_PENDING_USER : 가입 승인 대기 중"),
//            @ApiResponse(responseCode = "403", description = "ACCEPT_REJECTED_USER : 가입 승인 거절"),
//            @ApiResponse(responseCode = "401", description = "SIGNUP_REQUIRED : 회원가입 필요")
//    })
//    public ResponseEntity<?> kakaoLogin(@RequestHeader("accessToken") String accessToken) {
//        MemberDetails memberDetails = authService.kakaoLogin(accessToken);
//
//        if (memberDetails != null) {
//            // 유저 정보와 토큰 생성
//            Map<String, Response> result = memberService.signIn(memberDetails);
//            String token = result.keySet().iterator().next();
//
//            // 헤더에 토큰 저장
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Authorization", "Bearer " + token);
//            return new ResponseEntity<>(result.get(token), headers, HttpStatus.OK);
//        }
//        throw new MemberException(ErrorCode.MEMBER_DEFAULT_ERROR);
//    }

    /*
     * * 회원가입
     *
     * @param : accessToken 카카오에서 발급받은 accessToken
     * */
    @PostMapping(path = "/signup", headers = "accessToken")
    @Operation(summary = "회원가입 및 부가정보 입력", description = "회원가입을 시도한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청 dto 필드값 오류",
                         content = @Content(schema = @Schema(implementation = FieldValidationExceptionResponse.class))),
            @ApiResponse(responseCode = "500", description = "SIGNUP_FAILURE : 서버 오류",
                         content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<MemberInfoDTO.Response> signup(@RequestHeader("accessToken") String accessToken,
                                                         @Valid @RequestBody MemberInfoDTO.Request requestDTO) {
        log.info("---------------회원가입 시도-----------");

        MemberInfoDTO.Response responseDTO;

        responseDTO = authService.signup(accessToken, requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /*
     * * 닉네임 중복 검사
     * */
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복검사", description = "닉네임 중복검사를 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용가능한 닉네임"),
            @ApiResponse(responseCode = "400", description = "중복된 닉네임")
    })
    public ResponseEntity<?> checkUniqueNickname(@RequestParam("nickname") String nickname) {
        log.info("---------------닉네임 중복검사-----------");
        Boolean isNicknameDuplication = authService.isNicknameDuplication(nickname);
        if (!isNicknameDuplication) {
            return new ResponseEntity<>(nickname + " 은(는) 사용가능한 닉네임입니다.", HttpStatus.OK);
        }
        return new ResponseEntity<>(nickname + " 은(는) 중복된 닉네임입니다.", HttpStatus.BAD_REQUEST);
    }
}
