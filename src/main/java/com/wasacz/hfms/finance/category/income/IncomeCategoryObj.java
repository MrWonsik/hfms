package com.wasacz.hfms.finance.category.income;

import com.wasacz.hfms.finance.category.AbstractCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class IncomeCategoryObj extends AbstractCategory {

    @Builder
    protected IncomeCategoryObj(String categoryName, String colorHex, Boolean isFavourite) {
        super(categoryName, colorHex, isFavourite);
    }
}
