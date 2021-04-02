package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.CreateExpenseCategoryRequest;
import com.wasacz.hfms.utils.ValidatorUtils;

import static com.wasacz.hfms.utils.HexColorUtils.*;

public class ExpenseCategoryValidator {

    public static void validate(CreateExpenseCategoryRequest createRequest) {
        validateName(createRequest.getCategoryName());
        validateHexColor(createRequest.getColorHex());
    }

    private static void validateName(String categoryName) {
        ValidatorUtils.handleFieldBlank(categoryName, "categoryName");
    }

    private static void validateHexColor(String hexColor) {
        if(hexColor == null) {
            return;
        }
        if(isNotCorrectHexColor(hexColor)) {
            throw new IllegalArgumentException("Incorrect hex color provided.");
        }
    }

}
