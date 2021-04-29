package com.wasacz.hfms.finance.transaction.expense;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpensePositionObj {
     private final String positionName;
     private final Double size;
     private final Double cost;
}
