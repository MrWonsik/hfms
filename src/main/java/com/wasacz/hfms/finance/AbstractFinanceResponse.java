package com.wasacz.hfms.finance;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractFinanceResponse {
    private final Long id;
    private final String expenseName;
    private final Double cost;
    private final DateTime createdDate;
}
