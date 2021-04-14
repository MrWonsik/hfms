package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.persistence.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseCategoryVersionService {

    private final ExpenseCategoryVersionRepository expenseCategoryVersionRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCategorySaver expenseCategorySaver;

    public ExpenseCategoryVersionService(ExpenseCategoryVersionRepository expenseCategoryVersionRepository, ExpenseCategoryRepository expenseCategoryRepository, ExpenseCategorySaver expenseCategorySaver) {
        this.expenseCategoryVersionRepository = expenseCategoryVersionRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseCategorySaver = expenseCategorySaver;
    }

    public ExpenseCategoryVersion getCurrentCategoryVersion(ExpenseCategory expenseCategory) {
        if (expenseCategory == null) {
            throw new IllegalStateException("Expense category is null.");
        }
        List<ExpenseCategoryVersion> expenseCategoryVersions = expenseCategoryVersionRepository
                .findByExpenseCategory(expenseCategory).orElseThrow(() -> {
                    throw new IllegalStateException("Expense category versions is empty!");
                });
        return obtainCurrentCategoryVersion(expenseCategoryVersions);
    }

    private ExpenseCategoryVersion obtainCurrentCategoryVersion(List<ExpenseCategoryVersion> expenseCategoryVersions) {
        Optional<ExpenseCategoryVersion> newestVersionOptional = expenseCategoryVersions
                .stream()
                .filter(expenseCategoryVersion -> expenseCategoryVersion.getValidMonth().isBefore(YearMonth.now().plusMonths(1)))
                .max(Comparator.comparing(ExpenseCategoryVersion::getValidMonth));
        if(newestVersionOptional.isEmpty()) {
            throw new IllegalStateException("Not found current version!");
        }
        return newestVersionOptional.get();
    }



    public List<ExpenseCategoryVersion> getCategoryVersions(ExpenseCategory expenseCategory) {
        return expenseCategoryVersionRepository
                .findByExpenseCategory(expenseCategory).orElse(Collections.emptyList());
    }

    public ExpenseCategoryVersion addNewVersionForNextMonth(User user, long categoryId, Double newMaximumCost) {
        CategoryValidator.validateMaximumCost(BigDecimal.valueOf(newMaximumCost));
        ExpenseCategory category = findExpenseCategoryByIdAndUser(categoryId, user);
        YearMonth nextMonthFromNow = getNextMonthFromNow();
        Optional<ExpenseCategoryVersion> expenseCategoryVersionOptional = expenseCategoryVersionRepository.findByExpenseCategoryAndValidMonth(category, nextMonthFromNow);
        return expenseCategoryVersionOptional
                .map(categoryVersion -> updateCategory(categoryVersion, newMaximumCost))
                .orElseGet(() -> expenseCategorySaver.saveExpenseCategoryVersion(BigDecimal.valueOf(newMaximumCost), category, nextMonthFromNow));
    }

    private YearMonth getNextMonthFromNow() {
        return YearMonth.now().plusMonths(1);
    }

    public ExpenseCategoryVersion editCategoryVersion(User user, long categoryId, Double newMaximumCost) {
        CategoryValidator.validateMaximumCost(BigDecimal.valueOf(newMaximumCost));
        ExpenseCategory category = findExpenseCategoryByIdAndUser(categoryId, user);
        ExpenseCategoryVersion currentCategoryVersion = getCurrentCategoryVersion(category);
        return updateCategory(currentCategoryVersion, newMaximumCost);
    }

    private ExpenseCategory findExpenseCategoryByIdAndUser(long categoryId, User user) {
        return expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
    }

    private ExpenseCategoryVersion updateCategory(ExpenseCategoryVersion expenseCategoryVersion, Double newMaximumCost) {
        expenseCategoryVersion.setMaximumCost(BigDecimal.valueOf(newMaximumCost));
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }
}
