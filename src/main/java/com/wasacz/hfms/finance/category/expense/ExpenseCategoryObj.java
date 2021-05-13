package com.wasacz.hfms.finance.category.expense;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.wasacz.hfms.finance.category.AbstractCategory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@JsonTypeName("EXPENSE")
@Getter
public class ExpenseCategoryObj extends AbstractCategory {
    private final BigDecimal maximumAmount;

    @Builder
    protected ExpenseCategoryObj(String categoryName, String colorHex, Boolean isFavourite, String type, BigDecimal maximumAmount) {
        super(categoryName, colorHex, isFavourite, type);
        this.maximumAmount = maximumAmount;
    }
}
