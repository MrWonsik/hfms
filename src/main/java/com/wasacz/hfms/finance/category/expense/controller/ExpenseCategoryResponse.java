package com.wasacz.hfms.finance.category.expense.controller;


import com.wasacz.hfms.finance.category.controller.AbstractCategoryResponse;
import com.wasacz.hfms.utils.date.DateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ExpenseCategoryResponse extends AbstractCategoryResponse {
    private final ExpenseCategoryVersionResponse currentVersion;
    private final List<ExpenseCategoryVersionResponse> expenseCategoryVersions;

    @Builder
    protected ExpenseCategoryResponse(long id,
                                      String categoryName,
                                      String colorHex,
                                      boolean isFavourite,
                                      boolean isDeleted,
                                      DateTime createDate,
                                      ExpenseCategoryVersionResponse currentVersion,
                                      List<ExpenseCategoryVersionResponse> expenseCategoryVersions,
                                      Map<YearMonth, Double> summaryTransactionMap) {
        super(id, categoryName, colorHex, isFavourite, isDeleted, createDate, summaryTransactionMap);
        this.currentVersion = currentVersion;
        this.expenseCategoryVersions = expenseCategoryVersions;
    }
}
