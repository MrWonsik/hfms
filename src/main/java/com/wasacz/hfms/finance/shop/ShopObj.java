package com.wasacz.hfms.finance.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ShopObj {
    private final Long id;
    private final String name;
}
