package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.AbstractCategory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ExpenseCategoryObj extends AbstractCategory {
    private final BigDecimal maximumCost;

    @Builder
    protected ExpenseCategoryObj(String categoryName, String colorHex, Boolean isFavourite, BigDecimal maximumCost) {
        super(categoryName, colorHex, isFavourite);
        this.maximumCost = maximumCost;
    }
}
