package com.wasacz.hfms.expense.controller;

import com.wasacz.hfms.utils.date.DateTime;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopResponse {
    private final Long id;
    private final String shopName;
    private final boolean isDeleted;
    private final DateTime createDate;
}
