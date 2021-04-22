package com.wasacz.hfms.finance.shop;

import com.wasacz.hfms.finance.shop.CreateShopRequest;
import com.wasacz.hfms.utils.ValidatorUtils;

public class ShopValidator {

    public static void validate(CreateShopRequest createShopRequest) {
        validateName(createShopRequest.getShopName());
    }

    private static void validateName(String shopName) {
        ValidatorUtils.handleFieldBlank(shopName, "shopName");
    }
}
