package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.ExpenseCategoriesResponse;
import com.wasacz.hfms.expense.controller.ShopsResponse;
import com.wasacz.hfms.utils.date.DateTime;
import com.wasacz.hfms.expense.controller.CreateExpenseCategoryRequest;
import com.wasacz.hfms.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpenseCategoryRepository;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wasacz.hfms.utils.HexColorUtils.getRandomHexColor;

@Service
public class ExpenseCategoryManagementService {
    //TODO: add, delete, edit, getAll

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryManagementService(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public ExpenseCategoryResponse addExpenseCategory(CreateExpenseCategoryRequest request, User user) {
        ExpenseCategoryValidator.validate(request);
        ExpenseCategory expenseCategory = ExpenseCategory.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex() != null ? request.getColorHex() : getRandomHexColor())
                .isFavourite(request.getIsFavourite() != null ? request.getIsFavourite() : false)
                .user(user)
                .build();
        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        return getExpenseCategoryResponse(savedExpenseCategory);
    }

    private ExpenseCategoryResponse getExpenseCategoryResponse(ExpenseCategory expenseCategory) {
        return ExpenseCategoryResponse.builder()
                .id(expenseCategory.getId())
                .categoryName(expenseCategory.getCategoryName())
                .colorHex(expenseCategory.getColorHex())
                .isDeleted(expenseCategory.getIsDeleted())
                .isFavourite(expenseCategory.getIsFavourite())
                .createDate(new DateTime(expenseCategory.getCreatedDate()))
                .build();
    }

    public ExpenseCategoriesResponse getAllExpenseCategory(User user) {
        List<ExpenseCategory> expenseCategories = expenseCategoryRepository.findAllByUserAndIsDeletedFalse(user).orElse(Collections.emptyList());
        return new ExpenseCategoriesResponse(expenseCategories.stream().map(this::getExpenseCategoryResponse).collect(Collectors.toList()));
    }
}
