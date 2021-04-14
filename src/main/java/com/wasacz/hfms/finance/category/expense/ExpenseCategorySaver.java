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
        return saveExpenseCategoryVersion(expenseCategoryObj.getMaximumCost(), savedExpenseCategory);
    }

    private ExpenseCategory buildExpenseCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        return ExpenseCategory.builder()
                .categoryName(expenseCategoryObj.getCategoryName())
                .colorHex(expenseCategoryObj.getColorHex() != null ? expenseCategoryObj.getColorHex() : getRandomHexColor())
                .isFavourite(expenseCategoryObj.getIsFavourite() != null ? expenseCategoryObj.getIsFavourite() : false)
                .user(user)
                .build();
    }

    public ExpenseCategoryVersion saveExpenseCategoryVersion(BigDecimal maximumCost, ExpenseCategory expenseCategoryPersistence) {
        ExpenseCategoryVersion expenseCategoryVersion = buildExpenseCategoryVersionPersistence(maximumCost, expenseCategoryPersistence);
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }

    public ExpenseCategoryVersion saveExpenseCategoryVersion(BigDecimal maximumCost, ExpenseCategory expenseCategoryPersistence, YearMonth validMonth) {
        ExpenseCategoryVersion expenseCategoryVersion = buildExpenseCategoryVersionPersistence(maximumCost, expenseCategoryPersistence, validMonth);
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }

    private ExpenseCategoryVersion buildExpenseCategoryVersionPersistence(BigDecimal maximumCost, ExpenseCategory expenseCategoryPersistence) {
        return ExpenseCategoryVersion.builder()
                .expenseCategory(expenseCategoryPersistence)
                .maximumCost(maximumCost != null ? maximumCost : BigDecimal.ZERO)
                .build();
    }

    private ExpenseCategoryVersion buildExpenseCategoryVersionPersistence(BigDecimal maximumCost, ExpenseCategory expenseCategoryPersistence, YearMonth validMonth) {
        return ExpenseCategoryVersion.builder()
                .expenseCategory(expenseCategoryPersistence)
                .maximumCost(maximumCost != null ? maximumCost : BigDecimal.ZERO)
                .validMonth(validMonth)
                .build();
    }

}
