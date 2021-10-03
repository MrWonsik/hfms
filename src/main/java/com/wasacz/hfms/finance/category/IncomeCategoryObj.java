package com.wasacz.hfms.finance.category;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Getter;

@JsonTypeName("INCOME")
@Getter
public class IncomeCategoryObj extends AbstractCategory {

    @Builder
    protected IncomeCategoryObj(String categoryName, String colorHex, Boolean isFavourite, String type) {
        super(categoryName, colorHex, isFavourite, "INCOME");
    }
}
