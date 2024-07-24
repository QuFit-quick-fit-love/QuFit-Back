package com.cupid.qufit.global.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paging {
    private int pageNumber;
    private int pageSize;
}
