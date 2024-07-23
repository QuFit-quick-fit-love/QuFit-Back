package com.cupid.qufit.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberSigninDTO {
    @Getter
    @Builder
    public static class response {
        private String email;
        private String nickname;
        private String profileImage;
        private String gender;
        private String accessToken;
    }
}
