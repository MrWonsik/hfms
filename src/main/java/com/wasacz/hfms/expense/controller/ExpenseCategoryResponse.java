package com.wasacz.hfms.expense.controller;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseCategoryResponse {
    private final long id;
    private final String categoryName;
    private final String colorHex;
    private final boolean isFavourite;
    private final boolean isDeleted;
    private final DateTime createDate;

}
