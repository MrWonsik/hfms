package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.wasacz.hfms.finance.category.LocalDateToYearMonthConverter.convertToYearMonth;
import static java.util.Optional.empty;

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

    public Optional<ExpenseCategoryVersion> getCurrentCategoryVersion(Long expenseCategoryId) {
        List<ExpenseCategoryVersion> expenseCategoryVersions = getCategoryVersions(expenseCategoryId);
        if(expenseCategoryVersions.isEmpty()) {
            return Optional.empty();
        }
        return obtainCurrentCategoryVersion(expenseCategoryVersions);
    }

    private Optional<ExpenseCategoryVersion> obtainCurrentCategoryVersion(List<ExpenseCategoryVersion> expenseCategoryVersions) {
        return expenseCategoryVersions
                .stream()
                .filter(expenseCategoryVersion -> convertToYearMonth(expenseCategoryVersion.getValidMonth()).isBefore(YearMonth.now().plusMonths(1)))
                .max(Comparator.comparing(ExpenseCategoryVersion::getValidMonth));
    }

    public List<ExpenseCategoryVersion> getCategoryVersions(Long expenseCategoryId) {
        return expenseCategoryVersionRepository
                .findByExpenseCategoryId(expenseCategoryId).orElse(Collections.emptyList());
    }

    public ExpenseCategoryVersion updateCategoryVersion(User user, long categoryId, Double newMaximumAmount, YearMonth validMonth) {
        CategoryValidator.validateMaximumAmount(BigDecimal.valueOf(newMaximumAmount));
        ExpenseCategory category = findExpenseCategoryByIdAndUser(categoryId, user);
        Optional<ExpenseCategoryVersion> expenseCategoryVersionOptional = getVersionByValidMonth(categoryId, validMonth);

        return expenseCategoryVersionOptional
                .map(categoryVersion -> updateCategoryVersion(categoryVersion, newMaximumAmount))
                .orElseGet(() -> expenseCategorySaver.saveExpenseCategoryVersion(BigDecimal.valueOf(newMaximumAmount), category, validMonth));
    }

    private Optional<ExpenseCategoryVersion> getVersionByValidMonth(long categoryId, YearMonth validMonth) {
        List<ExpenseCategoryVersion> categoryVersions = getCategoryVersions(categoryId);
        for (ExpenseCategoryVersion expenseCategoryVersion : categoryVersions) {
            YearMonth yearMonth = YearMonth.of(expenseCategoryVersion.getValidMonth().getYear(), expenseCategoryVersion.getValidMonth().getMonth());
            if (yearMonth.equals(validMonth)) {
                return Optional.of(expenseCategoryVersion);
            }
        }
        return empty();
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
