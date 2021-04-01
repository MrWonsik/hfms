package com.wasacz.hfms.expense.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateExpenseCategoryRequest {
    private String categoryName;
    private String hexColor;
    private Boolean isFavourite;
}
