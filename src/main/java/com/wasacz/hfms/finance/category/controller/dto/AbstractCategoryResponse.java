package com.wasacz.hfms.finance.category.controller.dto;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.YearMonth;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCategoryResponse {
    private final long id;
    private final String categoryName;
    private final String colorHex;
    private final boolean isFavourite;
    private final boolean isDeleted;
    private final DateTime createDate;
    private final Map<YearMonth, Double> summaryTransactionMap;
}
