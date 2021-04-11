package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpenseCategoryVersion;
import com.wasacz.hfms.persistence.ExpenseCategoryVersionRepository;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//TODO: add update current version, and add posibility to add version for next month..

@Service
public class ExpenseCategoryVersionService {

    private final ExpenseCategoryVersionRepository expenseCategoryVersionRepository;

    public ExpenseCategoryVersionService(ExpenseCategoryVersionRepository expenseCategoryVersionRepository) {
        this.expenseCategoryVersionRepository = expenseCategoryVersionRepository;
    }

    public ExpenseCategoryVersion saveCategory(ExpenseCategoryObj expenseCategoryObj, ExpenseCategory expenseCategoryPersistence) {
        ExpenseCategoryVersion expenseCategoryVersion = buildExpenseCategoryVersionPersistence(expenseCategoryObj, expenseCategoryPersistence);
        return expenseCategoryVersionRepository.save(expenseCategoryVersion);
    }

    public ExpenseCategoryVersionResponse getNewestCategoryVersion(ExpenseCategory expenseCategory) {
        if (expenseCategory == null) {
            throw new IllegalStateException("Expense category is null.");
        }
        List<ExpenseCategoryVersion> expenseCategoryVersions = expenseCategoryVersionRepository
                .findExpenseCategoryVersionsByExpenseCategoryEquals(expenseCategory).orElse(Collections.emptyList());
        if (expenseCategoryVersions.isEmpty()) {
            throw new IllegalStateException("Expense category versions is empty!");
        }
        ExpenseCategoryVersion newestVersion = Collections.max(expenseCategoryVersions, Comparator.comparing(ExpenseCategoryVersion::getValidMonth));
        return buildExpenseCategoryVersionResponse(newestVersion);
    }

    public ExpenseCategoryVersionResponse buildExpenseCategoryVersionResponse(ExpenseCategoryVersion expenseCategoryVersion) {
        return ExpenseCategoryVersionResponse.builder()
                .id(expenseCategoryVersion.getId())
                .maximumCost(expenseCategoryVersion.getMaximumCost().doubleValue())
                .isValid(expenseCategoryVersion.getMaximumCost().doubleValue() != 0)
                .createDate(new DateTime(expenseCategoryVersion.getCreatedDate()))
                .validMonth(expenseCategoryVersion.getValidMonth())
                .build();
    }

    private ExpenseCategoryVersion buildExpenseCategoryVersionPersistence(ExpenseCategoryObj expenseCategoryObj, ExpenseCategory expenseCategoryPersistence) {
        return ExpenseCategoryVersion.builder()
                .expenseCategory(expenseCategoryPersistence)
                .maximumCost(expenseCategoryObj.getMaximumCost() != null ? expenseCategoryObj.getMaximumCost() : BigDecimal.ZERO)
                .build();
    }

    public List<ExpenseCategoryVersionResponse> getCategoryVersions(ExpenseCategory expenseCategory) {
        List<ExpenseCategoryVersion> expenseCategoryVersions = expenseCategoryVersionRepository
                .findExpenseCategoryVersionsByExpenseCategoryEquals(expenseCategory).orElse(Collections.emptyList());
        return expenseCategoryVersions.stream().map(this::buildExpenseCategoryVersionResponse).collect(Collectors.toList());
    }
}
