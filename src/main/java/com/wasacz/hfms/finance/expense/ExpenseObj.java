package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.finance.AbstractFinance;
import com.wasacz.hfms.finance.shop.ShopObj;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ExpenseObj extends AbstractFinance {
    private final ShopObj shop;
    private final List<ExpensePositionObj> expensePositions;

    @Builder
    public ExpenseObj(Long id, Long categoryId, String expenseName, Double cost, ShopObj shop, List<ExpensePositionObj> expensePositions) {
        super(id, categoryId, expenseName, cost);
        this.shop = shop;
        this.expensePositions = expensePositions;
    }

}
