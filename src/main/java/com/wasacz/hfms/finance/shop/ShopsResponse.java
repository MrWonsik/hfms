package com.wasacz.hfms.finance.shop;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopsResponse {
    private List<ShopResponse> shops;
}
