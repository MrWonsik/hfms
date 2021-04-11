package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.finance.category.controller.CreateCategoryRequest;
import com.wasacz.hfms.finance.category.ICategoryManagementService;
import com.wasacz.hfms.persistence.*;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Service
public class ExpenseCategoryManagementService implements ICategoryManagementService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCategoryVersionService expenseCategoryVersionService;

    public ExpenseCategoryManagementService(ExpenseCategoryRepository expenseCategoryRepository, ExpenseCategoryVersionService expenseCategoryVersionService) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseCategoryVersionService = expenseCategoryVersionService;
    }

    @Override
    public ExpenseCategoryResponse addCategory(CreateCategoryRequest categoryRequest, User user) {
        ExpenseCategoryObj expenseCategoryObj = getExpenseCategoryObj(categoryRequest);
        CategoryValidator.validate(expenseCategoryObj);
        ExpenseCategoryVersion expenseCategoryVersionSaved = saveCategory(expenseCategoryObj, user);
        return getExpenseCategoryResponse(expenseCategoryVersionSaved.getExpenseCategory());
    }

    private ExpenseCategoryObj getExpenseCategoryObj(CreateCategoryRequest request) {
        return ExpenseCategoryObj.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex())
                .isFavourite(request.getIsFavourite())
                .maximumCost(BigDecimal.valueOf(Optional.ofNullable(request.getMaximumCost()).orElse(0d)))
                .build();
    }

    private ExpenseCategoryVersion saveCategory(ExpenseCategoryObj expenseCategoryObj, User user) {
        ExpenseCategory expenseCategoryPersistence = buildExpenseCategory(expenseCategoryObj, user);
        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategoryPersistence);
        return expenseCategoryVersionService.saveCategory(expenseCategoryObj, savedExpenseCategory);
    }

    @Override
    public ExpenseCategoryResponse setAsFavourite(long categoryId, boolean isFavourite, User user) {
        ExpenseCategory expenseCategoryToUpdate = expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
        expenseCategoryToUpdate.setIsFavourite(isFavourite);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategoryToUpdate);
        return getExpenseCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public ExpenseCategoryResponse deleteCategory(long expenseCategoryId, User user) {
        ExpenseCategory expenseCategory = expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(expenseCategoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
        expenseCategory.setIsDeleted(true);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        return getExpenseCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public CategoriesResponse getAllCategories(User user) {
        List<ExpenseCategory> expenseCategories = expenseCategoryRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new CategoriesResponse(expenseCategories.stream().map(this::getExpenseCategoryResponse).collect(Collectors.toList()));
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
                .currentVersion(expenseCategoryVersionService.getNewestCategoryVersion(expenseCategory))
                .expenseCategoryVersions(expenseCategoryVersionService.getCategoryVersions(expenseCategory))
                .createDate(new DateTime(expenseCategory.getCreatedDate()))
                .build();
    }
}
