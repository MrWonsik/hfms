package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.persistence.*;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Service
public class ExpenseCategoryManagementService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCategoryVersionService expenseCategoryVersionService;

    public ExpenseCategoryManagementService(ExpenseCategoryRepository expenseCategoryRepository, ExpenseCategoryVersionService expenseCategoryVersionService) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseCategoryVersionService = expenseCategoryVersionService;
    }

    public ExpenseCategoryResponse addExpenseCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        CategoryValidator.validate(expenseCategoryObj);
        ExpenseCategoryVersion expenseCategoryVersionSaved = saveCategory(expenseCategoryObj, user);
        return getExpenseCategoryResponse(expenseCategoryVersionSaved.getExpenseCategory());
    }

    private ExpenseCategoryVersion saveCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        ExpenseCategory expenseCategoryPersistence = buildExpenseCategory(expenseCategoryObj, user);
        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategoryPersistence);
        return expenseCategoryVersionService.saveCategory(expenseCategoryObj, savedExpenseCategory);
    }

    private ExpenseCategory buildExpenseCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        return ExpenseCategory.builder()
                .categoryName(expenseCategoryObj.getCategoryName())
                .colorHex(expenseCategoryObj.getColorHex() != null ? expenseCategoryObj.getColorHex() : getRandomHexColor())
                .isFavourite(expenseCategoryObj.getIsFavourite() != null ? expenseCategoryObj.getIsFavourite() : false)
                .user(user)
                .build();
    }

    private ExpenseCategoryResponse getExpenseCategoryResponse(ExpenseCategory expenseCategory) {
        return ExpenseCategoryResponse.builder()
                .id(expenseCategory.getId())
                .categoryName(expenseCategory.getCategoryName())
                .colorHex(expenseCategory.getColorHex())
                .isDeleted(expenseCategory.getIsDeleted())
                .isFavourite(expenseCategory.getIsFavourite())
                .currentVersion(expenseCategoryVersionService.getNewestCategoryVersion(expenseCategory)) //TODO: add service for expense versions!
                .expenseCategoryVersions(expenseCategoryVersionService.getCategoryVersions(expenseCategory))
                .createDate(new DateTime(expenseCategory.getCreatedDate()))
                .build();
    }

    public ExpenseCategoriesResponse getAllExpenseCategory(User user) {
        List<ExpenseCategory> expenseCategories = expenseCategoryRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new ExpenseCategoriesResponse(expenseCategories.stream().map(this::getExpenseCategoryResponse).collect(Collectors.toList()));
    }

    public ExpenseCategoryResponse setAsFavourite(long expenseCategoryId, boolean isFavourite, User user) {
        ExpenseCategory expenseCategoryToUpdate = expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(expenseCategoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
        expenseCategoryToUpdate.setIsFavourite(isFavourite);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategoryToUpdate);
        return getExpenseCategoryResponse(updatedExpenseCategory);
    }

    public ExpenseCategoryResponse deleteExpenseCategory(long expenseCategoryId, User user) {
        ExpenseCategory expenseCategory = expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(expenseCategoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
        expenseCategory.setIsDeleted(true);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        return getExpenseCategoryResponse(updatedExpenseCategory);
    }
}
