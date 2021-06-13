package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.income.IncomeCategoryObj;
import com.wasacz.hfms.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.wasacz.hfms.utils.HexColorUtils.*;

@Slf4j
public class CategoryValidator {

    public static void validate(ExpenseCategoryObj expenseCategory) {
        validateName(expenseCategory.getCategoryName());
        validateHexColor(expenseCategory.getColorHex());
        validateMaximumAmount(expenseCategory.getMaximumAmount());
    }

    public static void validate(IncomeCategoryObj incomeCategory) {
        validateName(incomeCategory.getCategoryName());
        validateHexColor(incomeCategory.getColorHex());
    }

    public static void validateBeforeEdit(String categoryName, String hexColor) {
        if(categoryName != null) {
            validateName(categoryName);
        }
        validateHexColor(hexColor);
    }

    public static void validateMaximumAmount(BigDecimal maximumAmount) {
        if(maximumAmount == null) {
            return;
        }
        if(maximumAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("Maximum amount should be grater than 0. Provided: " + maximumAmount);
            throw new IllegalArgumentException("Maximum amount should be grater than 0.");
        }
    }

    private static void validateName(String categoryName) {
        ValidatorUtils.handleFieldBlank(categoryName, "categoryName");
    }

    public static void validateHexColor(String hexColor) {
        if(hexColor == null) {
            return;
        }
        if(isNotCorrectHexColor(hexColor)) {
            log.debug("Incorrect hex color provided. Provided:" + hexColor);
            throw new IllegalArgumentException("Incorrect hex color provided.");
        }
    }

}
