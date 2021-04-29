package com.wasacz.hfms.finance.shop;

import com.wasacz.hfms.utils.ValidatorUtils;

public class ShopValidator {

    public static void validate(ShopObj shopObj) {
        validateName(shopObj.getShopName());
    }

    private static void validateName(String shopName) {
        ValidatorUtils.handleFieldBlank(shopName, "shopName");
    }
}
