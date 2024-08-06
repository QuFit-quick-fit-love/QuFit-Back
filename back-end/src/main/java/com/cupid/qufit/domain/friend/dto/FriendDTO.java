package com.cupid.qufit.domain.friend.dto;

import lombok.Builder;
import lombok.Getter;

public class FriendDTO {

    @Getter
    @Builder
    public static class Response {

        private Long id;
        private String nickname;
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
