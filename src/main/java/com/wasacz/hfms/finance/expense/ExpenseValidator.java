package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.finance.AbstractFinance;
import com.wasacz.hfms.utils.ValidatorUtils;

public class ExpenseValidator {

    public static void validateFinance(AbstractFinance expenseObj) {
        ValidatorUtils.handleFieldBlank(expenseObj.getExpenseName(), "expenseName");
        isNotNullAndBiggerThanZero(expenseObj.getCost(), "Cost must be bigger than 0.");
    }

    public static void validateExpensePosition(ExpensePositionObj expensePositionObj) {
        ValidatorUtils.handleFieldBlank(expensePositionObj.getPositionName(), "positionName");
        isNotNullAndBiggerThanZero(expensePositionObj.getSize(), "Size must be bigger than 0.");
        isNotNullAndBiggerThanZero(expensePositionObj.getCost(), "Cost must be bigger than 0.");
    }

    private static void isNotNullAndBiggerThanZero(Double val, String message) {
        if (val == null || val <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
