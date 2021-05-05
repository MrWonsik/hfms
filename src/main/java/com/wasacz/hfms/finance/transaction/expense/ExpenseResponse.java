package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.transaction.AbstractTransactionResponse;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ExpenseResponse extends AbstractTransactionResponse {


    private final String shopName;
    private final List<ExpensePositionResponse> expensePositionList;
    private final Long receiptId;

    @Builder
    private ExpenseResponse(Long id, String expenseName, Double cost, LocalDate createdDate, String shopName, List<ExpensePositionResponse> expensePositionList, Long receiptId, String categoryName) {
        super(id, expenseName, cost, createdDate, categoryName);
        this.shopName = shopName;
        this.expensePositionList = expensePositionList;
        this.receiptId = receiptId;
    }
}
