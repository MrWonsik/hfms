package com.wasacz.hfms.finance.category.expense.controller;

import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryVersionService;
import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.utils.date.DateTime;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//TODO: improve it and use it... (need to work on interfaces)

@Component
public class ExpenseCategoryResponseMapper {

    private final ExpenseCategoryVersionService expenseCategoryVersionService;
    private final ExpenseCategoryVersionMapper expenseCategoryVersionMapper;

    public ExpenseCategoryResponseMapper(ExpenseCategoryVersionService expenseCategoryVersionService, ExpenseCategoryVersionMapper expenseCategoryVersionMapper) {
        this.expenseCategoryVersionService = expenseCategoryVersionService;
        this.expenseCategoryVersionMapper = expenseCategoryVersionMapper;
    }

    public ExpenseCategoryResponse mapToExpenseCategoryResponse(ExpenseCategory expenseCategory) {
        return ExpenseCategoryResponse.builder()
                .id(expenseCategory.getId())
                .categoryName(expenseCategory.getCategoryName())
                .colorHex(expenseCategory.getColorHex())
                .isDeleted(expenseCategory.getIsDeleted())
                .isFavourite(expenseCategory.getIsFavourite())
                .currentVersion(expenseCategoryVersionMapper.mapExpenseCategoryVersionToResponse(expenseCategoryVersionService.getCurrentCategoryVersion(expenseCategory)))
                .expenseCategoryVersions(expenseCategoryVersionMapper.mapExpenseCategoryVersionsListToResponse(expenseCategoryVersionService.getCategoryVersions(expenseCategory)))
                .createDate(new DateTime(expenseCategory.getCreatedDate()))
                .build();
    }

    public CategoriesResponse mapToExpenseCategoriesList(List<ExpenseCategory> expenseCategoryList) {
        return new CategoriesResponse(expenseCategoryList.stream().map(this::mapToExpenseCategoryResponse).collect(Collectors.toList()));
    }
}
