package com.cupid.qufit.domain.member.controller;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import com.cupid.qufit.domain.member.service.MemberService;
import com.cupid.qufit.entity.Member;
import com.cupid.qufit.entity.MemberStatus;
import com.cupid.qufit.global.common.response.CommonResultResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qufit/member")
@Tag(name = "MyPage", description = "마이페이지 관련 API")
@Log4j2
public class MemberController {

    private final MemberService memberService;

    /*
     * * 회원 정보 조회
     * */
    @GetMapping
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal MemberDetails memberDetails) {
        Long currentMemberId = memberDetails.getId();
        MemberInfoDTO.Response response = memberService.getMemberInfo(currentMemberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
     * * 회원 정보 수정
     * */
    @PutMapping
    public ResponseEntity<?> updateMemberInfo(@Valid @RequestBody MemberInfoDTO.Request request,
                                              @AuthenticationPrincipal MemberDetails memberDetails) {
        Long currentMemberId = memberDetails.getId();
        MemberInfoDTO.Response response = memberService.updateMemberInfo(request, currentMemberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
     * * 회원 탈퇴
     * */
    @DeleteMapping
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        Long currentMemberId = memberDetails.getId();

        Member member = memberService.deleteService(currentMemberId);

        if (member.getStatus() != MemberStatus.WITHDRAWN) {
            CommonResultResponse response = CommonResultResponse.builder()
                                                                .isSuccess(false)
                                                                .message("탈퇴 처리 되지 않았습니다.")
                                                                .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        CommonResultResponse response = CommonResultResponse.builder()
                                                            .isSuccess(true)
                                                            .message("탈퇴 처리 되었습니다.")
                                                            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
