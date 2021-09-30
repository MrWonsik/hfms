package com.wasacz.hfms.finance.transaction.income;

import com.wasacz.hfms.finance.transaction.AbstractTransaction;
import lombok.Builder;

import java.time.LocalDate;

public class IncomeObj extends AbstractTransaction {
    @Builder
    protected IncomeObj(Long id, Long categoryId, String name, Double amount, LocalDate transactionDate) {
        super(id, categoryId, name, amount, "INCOME", transactionDate);

    }
}
