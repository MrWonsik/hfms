package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.CategoryValidator;
import com.wasacz.hfms.finance.category.controller.CategoryObj;
import com.wasacz.hfms.finance.category.ICategoryManagementService;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryVersionMapper;
import com.wasacz.hfms.persistence.*;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseCategoryService implements ICategoryManagementService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseCategorySaver expenseCategorySaver;
    private final ExpenseCategoryVersionService expenseCategoryVersionService;
    private final ExpenseCategoryVersionMapper expenseCategoryVersionMapper;

    public ExpenseCategoryService(ExpenseCategoryRepository expenseCategoryRepository, ExpenseCategorySaver expenseCategorySaver, ExpenseCategoryVersionService expenseCategoryVersionService, ExpenseCategoryVersionMapper expenseCategoryVersionMapper) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseCategorySaver = expenseCategorySaver;
        this.expenseCategoryVersionService = expenseCategoryVersionService;
        this.expenseCategoryVersionMapper = expenseCategoryVersionMapper;
    }

    @Override
    public ExpenseCategoryResponse addCategory(CategoryObj categoryRequest, User user) {
        ExpenseCategoryObj expenseCategoryObj = getExpenseCategoryObj(categoryRequest);
        CategoryValidator.validate(expenseCategoryObj);
        ExpenseCategoryVersion expenseCategoryVersionSaved = expenseCategorySaver.saveExpenseCategory(expenseCategoryObj, user);
        return mapExpenseCategoryResponse(expenseCategoryVersionSaved.getExpenseCategory());
    }

    private ExpenseCategoryObj getExpenseCategoryObj(CategoryObj request) {
        return ExpenseCategoryObj.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex())
                .isFavourite(request.getIsFavourite())
                .maximumCost(BigDecimal.valueOf(Optional.ofNullable(request.getMaximumCost()).orElse(0d)))
                .build();
    }

    @Override
    public ExpenseCategoryResponse setAsFavourite(long categoryId, boolean isFavourite, User user) {
        ExpenseCategory expenseCategoryToUpdate = findByIdAndUser(categoryId, user);
        expenseCategoryToUpdate.setIsFavourite(isFavourite);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategoryToUpdate);
        return mapExpenseCategoryResponse(updatedExpenseCategory);
    }

    @Override
    public ExpenseCategoryResponse deleteCategory(long expenseCategoryId, User user) {
        ExpenseCategory expenseCategory = findByIdAndUser(expenseCategoryId, user);
        expenseCategory.setIsDeleted(true);
        ExpenseCategory updatedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        return mapExpenseCategoryResponse(updatedExpenseCategory);
    }

    public ExpenseCategory findByIdAndUser(long expenseCategoryId, User user) {
        return expenseCategoryRepository
                .findByIdAndUserAndIsDeletedFalse(expenseCategoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense category not found."));
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
        return mapExpenseCategoryResponse(updatedExpenseCategory);
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
                ) //TODO: improve it
                .expenseCategoryVersions(expenseCategoryVersionMapper.mapExpenseCategoryVersionsListToResponse(
                        expenseCategoryVersionService.getCategoryVersions(expenseCategory.getId()))
                ) //TODO: improve it
                .createDate(new DateTime(expenseCategory.getCreatedDate()))
                .build();
    }
}
