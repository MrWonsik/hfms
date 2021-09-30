package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
                    log.warn("Expense category " + expenseCategoryId + " versions is empty!");
                    throw new IllegalStateException("Expense category versions is empty!");
                });
        return obtainCurrentCategoryVersion(expenseCategoryVersions);
    }

    private ExpenseCategoryVersion obtainCurrentCategoryVersion(List<ExpenseCategoryVersion> expenseCategoryVersions) {
        Optional<ExpenseCategoryVersion> newestVersionOptional = expenseCategoryVersions
                .stream()
                .filter(expenseCategoryVersion -> YearMonth.of(expenseCategoryVersion.getValidMonth().getYear(), expenseCategoryVersion.getValidMonth().getMonth())
                        .isBefore(YearMonth.now().plusMonths(1)))
                .max(Comparator.comparing(ExpenseCategoryVersion::getValidMonth));
        if(newestVersionOptional.isEmpty()) {
            log.error("Not found current version!");
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

        if(currentCategoryVersion.getValidMonth().isBefore(YearMonth.now().atEndOfMonth())) {
            return addNewVersion(user, categoryId, newMaximumAmount, YearMonth.now());
        }
        return updateCategoryVersion(currentCategoryVersion, newMaximumAmount);
    }

    private ExpenseCategoryVersion addNewVersion(User user, long categoryId, Double newMaximumAmount, YearMonth validMonth) {
        ExpenseCategory category = findExpenseCategoryByIdAndUser(categoryId, user);
        Optional<ExpenseCategoryVersion> expenseCategoryVersionOptional = expenseCategoryVersionRepository.findByExpenseCategoryAndValidMonth(category, validMonth.atDay(1));
        return expenseCategoryVersionOptional
                .map(categoryVersion -> updateCategoryVersion(categoryVersion, newMaximumAmount))
                .orElseGet(() -> expenseCategorySaver.saveExpenseCategoryVersion(BigDecimal.valueOf(newMaximumAmount), category, validMonth));
    }

    private ExpenseCategory findExpenseCategoryByIdAndUser(long categoryId, User user) {
        return expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> {
                    log.warn("Expense category " + categoryId + " not found");
                    throw new IllegalArgumentException("Expense category not found.");
                });
    }

    private ExpenseCategoryVersion updateCategoryVersion(ExpenseCategoryVersion expenseCategoryVersion, Double newMaximumAmount) {
        expenseCategoryVersion.setMaximumAmount(BigDecimal.valueOf(newMaximumAmount));
        ExpenseCategoryVersion saved = expenseCategoryVersionRepository.save(expenseCategoryVersion);
        log.debug("Expense category version: " + saved.getExpenseCategory().getCategoryName() + " for month: " + expenseCategoryVersion.getValidMonth() + " has been updated");
        return saved;
    }
}
