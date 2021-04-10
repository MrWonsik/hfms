package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.CreateShopRequest;
import com.wasacz.hfms.utils.ValidatorUtils;

public class ShopValidator {

    public static void validate(CreateShopRequest createShopRequest) {
        validateName(createShopRequest.getShopName());
    }

    private static void validateName(String shopName) {
        ValidatorUtils.handleFieldBlank(shopName, "shopName");
    }
}
