package com.wasacz.hfms.finance.transaction.income;

import com.wasacz.hfms.finance.transaction.AbstractTransactionResponse;
import com.wasacz.hfms.finance.transaction.CategoryResponse;
import com.wasacz.hfms.finance.transaction.expense.ExpenseShopResponse;
import com.wasacz.hfms.finance.transaction.expense.expensePositions.ExpensePositionResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class IncomeResponse extends AbstractTransactionResponse {

    @Builder
    private IncomeResponse(Long id, String incomeName, Double amount, LocalDate createdDate, CategoryResponse category) {
        super(id, incomeName, amount, createdDate, category);
    }
}
