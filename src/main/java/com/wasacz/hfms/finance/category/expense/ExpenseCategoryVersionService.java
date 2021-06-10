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

    public ExpenseCategoryVersion getCurrentCategoryVersion(Long expenseCategoryId) {
        List<ExpenseCategoryVersion> expenseCategoryVersions = expenseCategoryVersionRepository
                .findByExpenseCategoryId(expenseCategoryId).orElseThrow(() -> {
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

    public List<ExpenseCategoryVersion> getCategoryVersions(Long expenseCategoryId) {
        return expenseCategoryVersionRepository
                .findByExpenseCategoryId(expenseCategoryId).orElse(Collections.emptyList());
    }

    public ExpenseCategoryVersion addNewVersionForNextMonth(User user, long categoryId, Double newMaximumAmount) {
        CategoryValidator.validateMaximumAmount(BigDecimal.valueOf(newMaximumAmount));
        return addNewVersion(user, categoryId, newMaximumAmount, getNextMonthFromNow());
    }

    private YearMonth getNextMonthFromNow() {
        return YearMonth.now().plusMonths(1);
    }

    public ExpenseCategoryVersion editCategoryVersion(User user, long categoryId, Double newMaximumAmount) {
        CategoryValidator.validateMaximumAmount(BigDecimal.valueOf(newMaximumAmount));
        findExpenseCategoryByIdAndUser(categoryId, user);
        ExpenseCategoryVersion currentCategoryVersion = getCurrentCategoryVersion(categoryId);

        if(currentCategoryVersion.getValidMonth().isBefore(YearMonth.now())) {
            return addNewVersion(user, categoryId, newMaximumAmount, YearMonth.now());
        }
        return updateCategory(currentCategoryVersion, newMaximumAmount);
    }

    private ExpenseCategoryVersion addNewVersion(User user, long categoryId, Double newMaximumAmount, YearMonth validMonth) {
        ExpenseCategory category = findExpenseCategoryByIdAndUser(categoryId, user);
        Optional<ExpenseCategoryVersion> expenseCategoryVersionOptional = expenseCategoryVersionRepository.findByExpenseCategoryAndValidMonth(category, validMonth);
        return expenseCategoryVersionOptional
                .map(categoryVersion -> updateCategory(categoryVersion, newMaximumAmount))
                .orElseGet(() -> expenseCategorySaver.saveExpenseCategoryVersion(BigDecimal.valueOf(newMaximumAmount), category, validMonth));
    }

    private ExpenseCategory findExpenseCategoryByIdAndUser(long categoryId, User user) {
        return expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
    }

    private ExpenseCategoryVersion updateCategory(ExpenseCategoryVersion expenseCategoryVersion, Double newMaximumAmount) {
        expenseCategoryVersion.setMaximumAmount(BigDecimal.valueOf(newMaximumAmount));
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }
}
