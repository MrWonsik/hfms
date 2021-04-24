package com.wasacz.hfms.finance.expense.Controller;

import com.wasacz.hfms.finance.AbstractFinanceResponse;
import com.wasacz.hfms.finance.expense.Controller.ExpensePositionResponse;
import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

import java.util.List;

@Getter
public class ExpenseResponse extends AbstractFinanceResponse {


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
