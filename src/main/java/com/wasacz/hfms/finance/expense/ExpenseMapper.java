package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.utils.date.DateTime;

import java.util.List;
import java.util.stream.Collectors;

public class ExpenseMapper {
    static ExpenseResponse mapExpenseToResponse(Expense expense, List<ExpensePosition> expensePositionList, Long receiptId) {
        return ExpenseResponse.builder()
                .expenseName(expense.getExpenseName())
                .shopName(expense.getShop() != null ? expense.getShop().getShopName() : null)
                .cost(expense.getCost().doubleValue())
                .createdDate(new DateTime(expense.getCreatedDate()))
                .expensePositionList(mapExpensePositionToResponse(expensePositionList))
                .receiptId(receiptId)
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