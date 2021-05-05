package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;

import java.util.List;
import java.util.stream.Collectors;

public class ExpenseMapper {
    static ExpenseResponse mapExpenseToResponse(Expense expense, List<ExpensePosition> expensePositionList, Long receiptId) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .expenseName(expense.getExpenseName())
                .shopName(expense.getShop() != null ? expense.getShop().getShopName() : null)
                .cost(expense.getCost().doubleValue())
                .createdDate(expense.getExpenseDate())
                .expensePositionList(mapExpensePositionToResponse(expensePositionList))
                .receiptId(receiptId)
                .categoryName(expense.getCategory().getCategoryName())
                .build();
    }

    static ExpenseResponse mapExpenseToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .expenseName(expense.getExpenseName())
                .shopName(expense.getShop() != null ? expense.getShop().getShopName() : null)
                .cost(expense.getCost().doubleValue())
                .createdDate(expense.getExpenseDate())
                .build();
    }

    private static List<ExpensePositionResponse> mapExpensePositionToResponse(List<ExpensePosition> expensePositionList) {
        return expensePositionList.stream().map(expensePosition -> ExpensePositionResponse
                .builder()
                .expensePositionName(expensePosition.getExpensePositionName())
                .cost(expensePosition.getCost().doubleValue())
                .size(expensePosition.getSize().doubleValue())
                .build()).collect(Collectors.toList());
    }
}
