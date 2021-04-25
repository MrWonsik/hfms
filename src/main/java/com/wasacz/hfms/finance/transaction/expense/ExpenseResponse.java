package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.transaction.AbstractTransactionResponse;
import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

import java.util.List;

@Getter
public class ExpenseResponse extends AbstractTransactionResponse {


    private final String shopName;
    private final List<ExpensePositionResponse> expensePositionList;
    private final Long receiptId;

    @Builder
    private ExpenseResponse(Long id, String expenseName, Double cost, DateTime createdDate, String shopName, List<ExpensePositionResponse> expensePositionList, Long receiptId) {
        super(id, expenseName, cost, createdDate);
        this.shopName = shopName;
        this.expensePositionList = expensePositionList;
        this.receiptId = receiptId;
    }
}
