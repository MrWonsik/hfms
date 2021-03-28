package com.wasacz.hfms.expense.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopsResponse {
    private final List<ShopResponse> shops;
}
