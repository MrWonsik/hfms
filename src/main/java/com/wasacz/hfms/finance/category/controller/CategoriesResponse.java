package com.wasacz.hfms.finance.category.controller;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesResponse {
    private List<? extends AbstractCategoryResponse> expenseCategories;
}
