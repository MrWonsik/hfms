package com.wasacz.hfms.finance.category.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpenseCategoryMaximumAmountRequest {
    private final Boolean isValidFromNextMonth;
    private final Double newMaximumAmount;
}
