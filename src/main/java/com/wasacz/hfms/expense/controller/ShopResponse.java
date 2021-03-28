package com.wasacz.hfms.expense.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopResponse {
    private final String shopName;
    private final boolean isDeleted;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate createDate;
}
