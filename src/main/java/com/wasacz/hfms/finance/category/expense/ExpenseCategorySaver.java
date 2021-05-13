package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.persistence.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Component
public class ExpenseCategorySaver {

    private final ExpenseCategoryVersionRepository expenseCategoryVersionRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategorySaver(ExpenseCategoryVersionRepository expenseCategoryVersionRepository, ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryVersionRepository = expenseCategoryVersionRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public ExpenseCategoryVersion saveExpenseCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        ExpenseCategory expenseCategoryPersistence = buildExpenseCategory(expenseCategoryObj, user);
        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategoryPersistence);
        return saveExpenseCategoryVersion(expenseCategoryObj.getMaximumAmount(), savedExpenseCategory);
    }

    private ExpenseCategory buildExpenseCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        return ExpenseCategory.builder()
                .categoryName(expenseCategoryObj.getCategoryName())
                .colorHex(expenseCategoryObj.getColorHex() != null ? expenseCategoryObj.getColorHex() : getRandomHexColor())
                .isFavourite(expenseCategoryObj.getIsFavourite() != null ? expenseCategoryObj.getIsFavourite() : false)
                .user(user)
                .build();
    }

    private ExpenseCategoryVersion saveExpenseCategoryVersion(BigDecimal maximumAmount, ExpenseCategory expenseCategoryPersistence) {
        ExpenseCategoryVersion expenseCategoryVersion = buildExpenseCategoryVersionPersistence(maximumAmount, expenseCategoryPersistence);
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }

    public ExpenseCategoryVersion saveExpenseCategoryVersion(BigDecimal maximumAmount, ExpenseCategory expenseCategoryPersistence, YearMonth validMonth) {
        ExpenseCategoryVersion expenseCategoryVersion = buildExpenseCategoryVersionPersistence(maximumAmount, expenseCategoryPersistence, validMonth);
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }

    private ExpenseCategoryVersion buildExpenseCategoryVersionPersistence(BigDecimal maximumAmount, ExpenseCategory expenseCategoryPersistence) {
        return ExpenseCategoryVersion.builder()
                .expenseCategory(expenseCategoryPersistence)
                .maximumAmount(maximumAmount != null ? maximumAmount : BigDecimal.ZERO)
                .build();
    }

    private ExpenseCategoryVersion buildExpenseCategoryVersionPersistence(BigDecimal maximumAmount, ExpenseCategory expenseCategoryPersistence, YearMonth validMonth) {
        return ExpenseCategoryVersion.builder()
                .expenseCategory(expenseCategoryPersistence)
                .maximumAmount(maximumAmount != null ? maximumAmount : BigDecimal.ZERO)
                .validMonth(validMonth)
                .build();
    }

}
