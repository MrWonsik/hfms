package com.wasacz.hfms.expense.controller;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseCategoryResponse {
    private final Long id;
    private final String categoryName;
    private final String hexColor;
    private final Boolean isFavourite;
    private final boolean isDeleted;
    private final DateTime createDate;

}
