package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.transaction.CategoryResponse;
import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.persistence.Shop;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseMapper {
    static ExpenseResponse mapExpenseToResponse(Expense expense) {
        return mapExpenseToResponse(expense, null, null);
    }

    static ExpenseResponse mapExpenseToResponse(Expense expense, List<ExpensePosition> expensePositionList, Long receiptId) {
        ExpenseCategory category = expense.getCategory();
        return ExpenseResponse.builder()
                .id(expense.getId())
                .expenseName(expense.getExpenseName())
                .shop(mapShopToResponse(expense.getShop()))
                .cost(expense.getCost().doubleValue())
                .createdDate(expense.getExpenseDate())
                .expensePositionList(mapExpensePositionToResponse(expensePositionList))
                .receiptId(receiptId)
                .category(CategoryResponse.builder().name(category.getCategoryName()).id(category.getId()).build())
                .build();
    }

    private static ExpenseShopResponse mapShopToResponse(Shop shop) {
        if(shop == null) {
            return null;
        }

        return ExpenseShopResponse.builder().id(shop.getId()).name(shop.getName()).build();
    }

    private static List<ExpensePositionResponse> mapExpensePositionToResponse(List<ExpensePosition> expensePositionList) {
        if(expensePositionList == null) {
            return Collections.emptyList();
        }
        return expensePositionList.stream().map(expensePosition -> ExpensePositionResponse
                .builder()
                .id(expensePosition.getId())
                .positionName(expensePosition.getExpensePositionName())
                .cost(expensePosition.getCost().doubleValue())
                .size(expensePosition.getSize().doubleValue())
                .build()).collect(Collectors.toList());
    }
}
