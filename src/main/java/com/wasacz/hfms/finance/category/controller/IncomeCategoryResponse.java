package com.wasacz.hfms.finance.category.controller;


import com.wasacz.hfms.utils.date.DateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.Map;

@Getter
@Setter
public class IncomeCategoryResponse extends AbstractCategoryResponse {

    @Builder
    protected IncomeCategoryResponse(long id,
                                     String categoryName,
                                     String colorHex,
                                     boolean isFavourite,
                                     boolean isDeleted,
                                     DateTime createDate,
                                     Map<YearMonth, Double> summaryTransactionMap) {
        super(id, categoryName, colorHex, isFavourite, isDeleted, createDate, summaryTransactionMap);
    }
}
