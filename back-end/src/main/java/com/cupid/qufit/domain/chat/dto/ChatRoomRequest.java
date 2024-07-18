package com.cupid.qufit.domain.chat.dto;

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
    private Long member1Id;
    private Long member2Id;
}