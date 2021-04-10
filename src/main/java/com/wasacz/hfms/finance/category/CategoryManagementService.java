package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryManagementService;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryResponse;
import com.wasacz.hfms.persistence.User;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CategoryManagementService {

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    public CategoryManagementService(ExpenseCategoryManagementService expenseCategoryManagementService) {
        this.expenseCategoryManagementService = expenseCategoryManagementService;
    }

    public AbstractCategoryResponse addCategory(CreateCategoryRequest request, User user) {
        if(request.getCategoryType() == null) {
            throw new IllegalArgumentException("Category is required.");
        }

        switch (request.getCategoryType()) {
            case EXPENSE -> {
                return addExpenseCategory(request, user);
            }
            case INCOME -> {
                return addIncomeCategory(request);
            }
            default -> throw new IllegalArgumentException("Category not exists.");
        }
    }

    private ExpenseCategoryResponse addExpenseCategory(CreateCategoryRequest request, User user) {
        var expenseCategory = ExpenseCategoryObj.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex())
                .isFavourite(request.getIsFavourite())
                .maximumCost(BigDecimal.valueOf(Optional.ofNullable(request.getMaximumCost()).orElse(0d)))
                .build();
        return expenseCategoryManagementService.addExpenseCategory(expenseCategory, user);
    }

    private AbstractCategoryResponse addIncomeCategory(CreateCategoryRequest request) {
        var incomeCategory = IncomeCategoryObj.builder()
                .categoryName(request.getCategoryName())
                .colorHex(request.getColorHex())
                .isFavourite(request.getIsFavourite())
                .build();
        throw new NotImplementedException("Not implemented yet.");
    }

    public AbstractCategoryResponse setAsFavourite(long categoryId, CategoryIsFavouriteRequest request, User user) {
        switch (request.getCategoryType()) {
            case EXPENSE -> {
                return expenseCategoryManagementService.setAsFavourite(categoryId, request.getIsFavourite(), user);
            }
            case INCOME -> {
                throw new NotImplementedException("Not implemented yet.");
            }
            default -> throw new IllegalArgumentException("Category not exists.");
        }
    }
}
