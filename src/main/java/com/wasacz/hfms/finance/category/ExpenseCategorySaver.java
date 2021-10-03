package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Component
@Slf4j
public class ExpenseCategorySaver {

    private final ExpenseCategoryVersionRepository expenseCategoryVersionRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    ExpenseCategorySaver(ExpenseCategoryVersionRepository expenseCategoryVersionRepository, ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryVersionRepository = expenseCategoryVersionRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    ExpenseCategoryVersion saveExpenseCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
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

    ExpenseCategoryVersion saveExpenseCategoryVersion(BigDecimal maximumAmount, ExpenseCategory expenseCategoryPersistence, YearMonth validMonth) {
        ExpenseCategoryVersion expenseCategoryVersion = buildExpenseCategoryVersionPersistence(maximumAmount, expenseCategoryPersistence, validMonth);
        ExpenseCategoryVersion saved = expenseCategoryVersionRepository.save(expenseCategoryVersion);
        log.debug("Expense category version: " + expenseCategoryPersistence.getCategoryName() + " for month: " + validMonth.toString() + " has been saved");
        return saved;
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
                .validMonth(validMonth.atDay(1))
                .build();
    }

}
