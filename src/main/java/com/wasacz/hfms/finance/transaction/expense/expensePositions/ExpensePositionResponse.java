package com.wasacz.hfms.finance.transaction.expense.expensePositions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpensePositionResponse {
    private final Long id;
    private final String positionName;
    private final Double size;
    private final Double amount;
}
