package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.*;
import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryVersionMapper;
import com.wasacz.hfms.finance.transaction.TransactionType;
import com.wasacz.hfms.persistence.*;
import com.wasacz.hfms.utils.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExpenseCategoryService implements ICategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCategorySaver expenseCategorySaver;
    private final ExpenseCategoryVersionService expenseCategoryVersionService;
    private final ExpenseCategoryVersionMapper expenseCategoryVersionMapper;
    private final TransactionSummaryProvider transactionSummaryProvider;

    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository, ExpenseCategorySaver expenseCategorySaver, ExpenseCategoryVersionService expenseCategoryVersionService, ExpenseCategoryVersionMapper expenseCategoryVersionMapper, TransactionSummaryProvider transactionSummaryProvider) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseCategorySaver = expenseCategorySaver;
        this.expenseCategoryVersionService = expenseCategoryVersionService;
        this.expenseCategoryVersionMapper = expenseCategoryVersionMapper;
        this.transactionSummaryProvider = transactionSummaryProvider;
    }

    @Override
    public ExpenseCategoryResponse addCategory(AbstractCategory categoryRequest, User user) {
        if(!(categoryRequest instanceof ExpenseCategoryObj)) {
            log.error("Provide incorrect object!");
            throw new IllegalStateException("Provide incorrect object!");
        }
        ExpenseCategoryObj expenseCategoryObj = (ExpenseCategoryObj) categoryRequest;
        CategoryValidator.validate(expenseCategoryObj);
        ExpenseCategoryVersion expenseCategoryVersionSaved = expenseCategorySaver.saveExpenseCategory(expenseCategoryObj, user);
        log.debug("Expense category version has been saved: %s for month: %s".formatted(expenseCategoryVersionSaved.getExpenseCategory().getCategoryName(), expenseCategoryVersionSaved.getValidMonth().toString()));
        return mapExpenseCategoryResponse(expenseCategoryVersionSaved.getExpenseCategory());
    }

    @Override
    public ExpenseCategoryResponse toggleFavourite(long categoryId, boolean isFavourite, User user) {
        ExpenseCategory expenseCategoryToUpdate = findByIdAndUser(categoryId, user);
        expenseCategoryToUpdate.setIsFavourite(isFavourite);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategoryToUpdate);
        log.debug("Expense category " + updatedExpenseCategory.getCategoryName() + " favourite field has been toggled to: " + isFavourite);
        return mapExpenseCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public ExpenseCategoryResponse deleteCategory(long expenseCategoryId, User user) {
        ExpenseCategory expenseCategory = findByIdAndUser(expenseCategoryId, user);
        expenseCategory.setIsDeleted(true);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        log.debug("Expense category " + updatedExpenseCategory.getCategoryName() + " has been deleted");
        return mapExpenseCategoryResponse(updatedExpenseCategory);
    }

    public ExpenseCategory findByIdAndUser(long expenseCategoryId, User user) {
        return expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(expenseCategoryId, user)
                .orElseThrow(() -> {
                    log.warn("Expense category " + expenseCategoryId + " not found.");
                    throw new IllegalArgumentException("Expense category not found.");
                });
    }

    @Override
    public CategoriesResponse getAllCategories(User user) {
        List<ExpenseCategory> expenseCategories = expenseCategoryRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new CategoriesResponse(expenseCategories.stream().map(this::mapExpenseCategoryResponse).collect(Collectors.toList()));
    }

    @Override
    public ExpenseCategoryResponse editCategory(long id, String newCategoryName, String newColorHex, User user) {
        CategoryValidator.validateBeforeEdit(newCategoryName, newColorHex);
        ExpenseCategory expenseCategory = findByIdAndUser(id, user);
        if(newColorHex == null && newCategoryName == null) {
            return mapExpenseCategoryResponse(expenseCategory);
        }
        if(newCategoryName != null) {
            expenseCategory.setCategoryName(newCategoryName);
        }
        if(newColorHex != null) {
            expenseCategory.setColorHex(newColorHex);
        }
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        log.debug("Expense category " + updatedExpenseCategory.getCategoryName() + " has been edited.");
        return mapExpenseCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public CategoryType getService() {
        return CategoryType.EXPENSE;
    }


    private ExpenseCategoryResponse mapExpenseCategoryResponse(ExpenseCategory expenseCategory) {
        return ExpenseCategoryResponse.builder()
                .id(expenseCategory.getId())
                .categoryName(expenseCategory.getCategoryName())
                .colorHex(expenseCategory.getColorHex())
                .isDeleted(expenseCategory.getIsDeleted())
                .isFavourite(expenseCategory.getIsFavourite())
                .currentVersion(expenseCategoryVersionMapper.mapExpenseCategoryVersionToResponse(
                        expenseCategoryVersionService.getCurrentCategoryVersion(expenseCategory.getId()))
                )
                .expenseCategoryVersions(expenseCategoryVersionMapper.mapExpenseCategoryVersionsListToResponse(
                        expenseCategoryVersionService.getCategoryVersions(expenseCategory.getId()))
                )
                .createDate(new DateTime(expenseCategory.getCreatedDate()))
                .summaryTransactionMap(transactionSummaryProvider.getTransactionMapProvider(expenseCategory.getId(), TransactionType.EXPENSE))
                .build();
    }
}
