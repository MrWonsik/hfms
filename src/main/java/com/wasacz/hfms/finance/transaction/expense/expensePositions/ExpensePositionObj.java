package com.wasacz.hfms.finance.transaction.expense.expensePositions;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpensePositionObj {
     private final Long id;
     private final String positionName;
     private final Double size;
     private final Double amount;
}
