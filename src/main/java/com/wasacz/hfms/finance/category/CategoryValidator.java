package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.income.IncomeCategoryObj;
import com.wasacz.hfms.utils.ValidatorUtils;

import java.math.BigDecimal;

import static com.wasacz.hfms.utils.HexColorUtils.*;

public class CategoryValidator {

    public static void validate(ExpenseCategoryObj expenseCategory) {
        validateName(expenseCategory.getCategoryName());
        validateHexColor(expenseCategory.getColorHex());
        validateMaximumCost(expenseCategory.getMaximumCost());
    }

    public static void validate(IncomeCategoryObj incomeCategory) {
        validateName(incomeCategory.getCategoryName());
        validateHexColor(incomeCategory.getColorHex());
    }

    public static void validateMaximumCost(BigDecimal maximumCost) {
        if(maximumCost == null) {
            return;
        }
        if(maximumCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Maximum cost should be grater than 0.");
        }
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
