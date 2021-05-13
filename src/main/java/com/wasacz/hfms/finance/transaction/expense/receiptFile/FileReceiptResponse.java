package com.wasacz.hfms.finance.transaction.expense.receiptFile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileReceiptResponse {
    private final Long id;
    private final String name;
    private final Long length;
    private final String base64Resource;
}
