package com.wasacz.hfms.finance.category.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EditCategoryRequest {
    private final String categoryName;
    private final String colorHex;
}
