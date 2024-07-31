package com.cupid.qufit.domain.admin.controller;

import com.cupid.qufit.domain.admin.dto.AdminMemberInfoDTO;
import com.cupid.qufit.domain.admin.dto.AdminMemberInfoDTO.Response;
import com.cupid.qufit.domain.admin.dto.AdminSignupApprovalDTO;
import com.cupid.qufit.domain.admin.service.AdminService;
import com.cupid.qufit.global.common.response.FieldValidationExceptionResponse;
import com.cupid.qufit.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qufit/admin")
@Tag(name = "Admin", description = "관리자 관련 API")
@Log4j2
public class AdminController {

    private final AdminService adminService;

    /*
     * * 관리자 회원 조회 (페이지네이션, 필터링)
     *
     * @param 필터링 조회할 멤버 상태 (PENDING(대기 중 회원), APPROVED(승인), WITHDRAWN(탈퇴))
     * */
    @GetMapping("/member")
    @Operation(summary = "관리자 회원 조회", description = "멤버 상태에 따라 필터링하여 회원 조회 (PENDING(대기 중 회원), APPROVED(승인), WITHDRAWN(탈퇴))")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "RequestParam validation 에러",
                         content = @Content(schema = @Schema(implementation = FieldValidationExceptionResponse.class))),
    })
    public ResponseEntity<Page<AdminMemberInfoDTO.Response>> getMember(
            @PageableDefault(size = 5, sort = "id", direction = Direction.ASC) Pageable pageable,
            @RequestParam(required = false)
            @Pattern(regexp = "^(PENDING|APPROVED|WITHDRAWN)$", message = "올바른 status를 입력하세요(PENDING|APPROVED|WITHDRAWN)")
            @Schema(description = "회원 상태 (PENDING, APPROVED, WITHDRAWN)") String status) {
        Page<Response> responses;
        if (status == null) {
            // status가 지정되지 않으면 전체 회원 조회
            responses = adminService.getAllMember(pageable);
        } else {
            // status 지정되면 필터링 검색
            responses = adminService.getMemberByStatus(pageable, status);
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /*
     * * 관리자 회원가입 승인
     * */
    @PutMapping("/approve")
    @Operation(summary = "관리자 회원가입 승인 / 거절", description = "회원 가입 승인 / 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 / 거절 성공"),
            @ApiResponse(responseCode = "400", description = "MEMBER_STATUS_NOT_PENDING : 대기 중인 회원이 아님",
                         content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<AdminMemberInfoDTO.Response> approveMemberSignup(
            @RequestBody AdminSignupApprovalDTO.Request request) {
        AdminMemberInfoDTO.Response response = adminService.approveMemberSingup(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
