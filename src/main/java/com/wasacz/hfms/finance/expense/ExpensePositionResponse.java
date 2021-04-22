package com.wasacz.hfms.finance.expense;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpensePositionResponse {
    private final String expensePositionName;
    private final Double size;
    private final Double cost;
}
