package com.wasacz.hfms.finance.category.expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategoriesResponse {
    private List<ExpenseCategoryResponse> expenseCategories;
}
