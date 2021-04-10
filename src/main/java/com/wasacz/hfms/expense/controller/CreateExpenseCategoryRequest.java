package com.wasacz.hfms.expense.controller;

import lombok.Builder;
import lombok.Getter;


//TODO: change this request to CreateCategoryRequest - to create expesne and income categry

@Getter
@Builder
public class CreateExpenseCategoryRequest {
    private final String categoryName;
    private final String colorHex;
    private final Boolean isFavourite;
}
