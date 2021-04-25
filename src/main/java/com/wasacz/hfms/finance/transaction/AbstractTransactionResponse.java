package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTransactionResponse {
    private final Long id;
    private final String name;
    private final Double cost;
    private final DateTime createdDate;
}
