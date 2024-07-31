package com.cupid.qufit.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class AdminSignupApprovalDTO {

    /*
     * * 회원 가입 승인, 거절 요청 DTO
     * */
    @Getter
    @Schema(description = "회원가입 승인/ 거절 요청 객체")
    public static class Request {
        @Schema(description = "회원가입 승인/ 거절 할 회원의 ID")
        private Long memberId;
        @Schema(description = "회원가입 승인 : true, 거절 : false")
        private Boolean isApproved;
    }
}
