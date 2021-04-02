package com.wasacz.hfms.expense.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateExpenseCategoryRequest {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
}
