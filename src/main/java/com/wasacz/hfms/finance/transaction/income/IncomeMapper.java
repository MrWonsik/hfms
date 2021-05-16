package com.wasacz.hfms.finance.transaction.income;

import com.wasacz.hfms.finance.transaction.CategoryResponse;
import com.wasacz.hfms.persistence.Income;
import com.wasacz.hfms.persistence.IncomeCategory;

public class IncomeMapper {
    static IncomeResponse mapIncomeToResponse(Income income) {
        IncomeCategory category = income.getCategory();
        return IncomeResponse.builder()
                .id(income.getId())
                .incomeName(income.getIncomeName())
                .amount(income.getAmount().doubleValue())
                .createdDate(income.getIncomeDate())
                .category(CategoryResponse.builder().name(category.getCategoryName()).id(category.getId()).build())
                .build();
    }
}
