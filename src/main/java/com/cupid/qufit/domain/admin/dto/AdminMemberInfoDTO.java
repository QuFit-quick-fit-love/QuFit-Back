package com.cupid.qufit.domain.admin.dto;

import com.cupid.qufit.domain.member.dto.MemberInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminMemberInfoDTO {
    /*
     * * 관리자 회원정보 조회 응답 DTO
     * */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "[ AdminMemberInfoDTO ] 관리자 회원정보 조회 응답 DTO")
    public static class Response {
        @Schema(description = "회원 상태 (PENDING, APPROVED, WITHDRAWN")
        private String status;
        @Schema(description = "회원정보")
        private MemberInfoDTO.Response memberInfo;

    }
}
