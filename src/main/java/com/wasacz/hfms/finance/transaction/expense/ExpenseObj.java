package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.finance.transaction.AbstractTransaction;
import com.wasacz.hfms.finance.transaction.expense.expensePositions.ExpensePositionObj;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ExpenseObj extends AbstractTransaction {
    private final ShopObj shop;
    private final List<ExpensePositionObj> expensePositions;

    @Builder
    public ExpenseObj(Long id, Long categoryId, String expenseName, Double amount, ShopObj shop, List<ExpensePositionObj> expensePositions, LocalDate transactionDate) {
        super(id, categoryId, expenseName, amount, "EXPENSE", transactionDate);
        this.shop = shop;
        this.expensePositions = expensePositions;
    }

}
