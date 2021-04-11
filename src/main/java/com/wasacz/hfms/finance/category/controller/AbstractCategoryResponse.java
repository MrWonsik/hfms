package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCategoryResponse {
    private final long id;
    private final String categoryName;
    private final String colorHex;
    private final boolean isFavourite;
    private final boolean isDeleted;
    private final DateTime createDate;
}
