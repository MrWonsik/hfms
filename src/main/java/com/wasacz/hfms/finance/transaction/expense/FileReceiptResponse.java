package com.wasacz.hfms.finance.transaction.expense;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileReceiptResponse {
    private final String name;
    private final Long length;
    private final String base64Resource;
}
