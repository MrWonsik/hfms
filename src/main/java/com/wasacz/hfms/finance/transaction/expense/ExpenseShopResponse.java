package com.wasacz.hfms.finance.transaction.expense;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseShopResponse {
    private final Long id;
    private final String name;
}
