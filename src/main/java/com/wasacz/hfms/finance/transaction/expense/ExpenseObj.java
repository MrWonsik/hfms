package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.finance.transaction.AbstractTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ExpenseObj extends AbstractTransaction {
    private final ShopObj shop;
    private final List<ExpensePositionObj> expensePositions;

    @Builder
    public ExpenseObj(Long id, Long categoryId, String expenseName, Double cost, ShopObj shop, List<ExpensePositionObj> expensePositions, String transactionType) {
        super(id, categoryId, expenseName, cost, transactionType);
        this.shop = shop;
        this.expensePositions = expensePositions;
    }

}
