package com.wasacz.hfms.finance.transaction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTransactionResponse {
    private final Long id;
    private final String name;
    private final Double cost;
    private final LocalDate createdDate;
    private final CategoryResponse category;
}
