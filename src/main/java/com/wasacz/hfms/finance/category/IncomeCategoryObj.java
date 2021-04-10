package com.wasacz.hfms.finance.category;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IncomeCategoryObj extends AbstractCategory {

    @Builder
    protected IncomeCategoryObj(String categoryName, String colorHex, Boolean isFavourite) {
        super(categoryName, colorHex, isFavourite);
    }
}
