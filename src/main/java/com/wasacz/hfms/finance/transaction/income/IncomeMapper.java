package com.wasacz.hfms.finance.transaction.income;

import com.wasacz.hfms.finance.transaction.CategoryResponse;
import com.wasacz.hfms.finance.transaction.expense.ExpenseResponse;
import com.wasacz.hfms.finance.transaction.expense.ExpenseShopResponse;
import com.wasacz.hfms.finance.transaction.expense.expensePositions.ExpensePositionResponse;
import com.wasacz.hfms.persistence.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
