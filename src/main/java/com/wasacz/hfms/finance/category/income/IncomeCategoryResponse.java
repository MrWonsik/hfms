package com.wasacz.hfms.finance.category.income;


import com.wasacz.hfms.finance.category.controller.AbstractCategoryResponse;
import com.wasacz.hfms.utils.date.DateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncomeCategoryResponse extends AbstractCategoryResponse {

    @Builder
    protected IncomeCategoryResponse(long id,
                                     String categoryName,
                                     String colorHex,
                                     boolean isFavourite,
                                     boolean isDeleted,
                                     DateTime createDate) {
        super(id, categoryName, colorHex, isFavourite, isDeleted, createDate);
    }
}
