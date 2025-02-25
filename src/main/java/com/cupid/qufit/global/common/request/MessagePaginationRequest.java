package com.cupid.qufit.global.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagePaginationRequest {

    private String messageId; // ! 기준이 되는 메시지 ID
    private int pageNumber;
    private int pageSize;
}
