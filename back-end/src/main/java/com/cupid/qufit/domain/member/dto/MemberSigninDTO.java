package com.cupid.qufit.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberSigninDTO {
    @Getter
    @Builder
    public static class Response {
        private String email;
        private String nickname;
        private String profileImage;
        private Character gender;
    }
}
