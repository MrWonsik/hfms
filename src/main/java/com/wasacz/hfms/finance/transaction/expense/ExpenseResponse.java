package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.transaction.AbstractTransactionResponse;
import com.wasacz.hfms.finance.transaction.CategoryResponse;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ExpenseResponse extends AbstractTransactionResponse {


    private final ExpenseShopResponse shop;
    private final List<ExpensePositionResponse> expensePositionList;
    private final Long receiptId;

    @Builder
    private ExpenseResponse(Long id, String expenseName, Double cost, LocalDate createdDate, ExpenseShopResponse shop, List<ExpensePositionResponse> expensePositionList, Long receiptId, CategoryResponse category) {
        super(id, expenseName, cost, createdDate, category);
        this.shop = shop;
        this.expensePositionList = expensePositionList;
        this.receiptId = receiptId;
    }
}
