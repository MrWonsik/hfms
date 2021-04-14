package com.wasacz.hfms.finance.category.expense.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseCategoryMaximumCostRequest {
    private final Boolean isValidFromNextMonth;
    private final Double newMaximumCost;
}
