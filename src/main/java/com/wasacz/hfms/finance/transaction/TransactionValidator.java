package com.wasacz.hfms.finance.transaction;

import com.wasacz.hfms.finance.transaction.expense.expensePositions.ExpensePositionObj;
import com.wasacz.hfms.utils.ValidatorUtils;

public class TransactionValidator {

    public static void validateFinance(AbstractTransaction transaction) {
        ValidatorUtils.handleFieldIsNull(transaction.getTransactionDate(), "transaction date");
        ValidatorUtils.handleFieldBlank(transaction.getName(), "name");
        isNotNullAndBiggerThanZero(transaction.getAmount(), "Amount must be bigger than 0.");
    }

    public static void validateExpensePosition(ExpensePositionObj expensePositionObj) {
        ValidatorUtils.handleFieldBlank(expensePositionObj.getPositionName(), "positionName");
        isNotNullAndBiggerThanZero(expensePositionObj.getSize(), "Size must be bigger than 0.");
        isNotNullAndBiggerThanZero(expensePositionObj.getAmount(), "Amount must be bigger than 0.");
    }

    private static void isNotNullAndBiggerThanZero(Double val, String message) {
        if (val == null || val <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
