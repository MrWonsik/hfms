package com.wasacz.hfms.expense.controller;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopsResponse {
    private List<ShopResponse> shops;
}
