package com.cupid.qufit.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class FriendDTO {

    @Getter
    @Builder
    public static class Response {

        @Schema(description = "친구 아이디")
        private Long id;
        @Schema(description = "친구 닉네임")
        private String nickname;
        @Schema(description = "친구 프로필이미지")
        private String profileImage;

        public static Response of(Long id, String nickname, String profileImage) {
            return Response.builder()
                           .id(id)
                           .nickname(nickname)
                           .profileImage(profileImage)
                           .build();
        }
    }
}
