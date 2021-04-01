package com.wasacz.hfms.expense.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate createDate;
    @JsonFormat(pattern="HH:mm:ss")
    private final LocalTime createTime;

}
