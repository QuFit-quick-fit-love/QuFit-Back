package com.cupid.qufit.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * * 채팅방을 생성할 때 필요한 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    @Schema(description = "회원1의 ID")
    private Long member1Id;
    @Schema(description = "회원2의 ID")
    private Long member2Id;
}