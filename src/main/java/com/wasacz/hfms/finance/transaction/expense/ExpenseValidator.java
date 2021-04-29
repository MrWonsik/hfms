package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.transaction.AbstractTransaction;
import com.wasacz.hfms.utils.ValidatorUtils;

public class ExpenseValidator {

    public static void validateFinance(AbstractTransaction transaction) {
        ValidatorUtils.handleFieldBlank(transaction.getName(), "name");
        isNotNullAndBiggerThanZero(transaction.getCost(), "Cost must be bigger than 0.");
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
