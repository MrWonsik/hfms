package com.wasacz.hfms.finance.transaction.income;

import com.wasacz.hfms.finance.transaction.AbstractTransactionResponse;
import com.wasacz.hfms.finance.transaction.CategoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class IncomeResponse extends AbstractTransactionResponse {

    @Builder
    private IncomeResponse(Long id, String incomeName, Double amount, LocalDate createdDate, CategoryResponse category) {
        super(id, incomeName, amount, createdDate, category);
    }
}
