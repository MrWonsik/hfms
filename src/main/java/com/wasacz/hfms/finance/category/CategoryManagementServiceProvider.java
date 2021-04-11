package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.controller.AbstractCategoryResponse;
import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.controller.CategoryIsFavouriteRequest;
import com.wasacz.hfms.finance.category.controller.CreateCategoryRequest;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;


@Service
public class CategoryManagementServiceProvider {

    private final ICategoryManagementService expenseCategoryManagementService;
    private final ICategoryManagementService incomeCategoryManagementService;

    public CategoryManagementServiceProvider(ICategoryManagementService expenseCategoryManagementService, ICategoryManagementService incomeCategoryManagementService) {
        this.expenseCategoryManagementService = expenseCategoryManagementService;
        this.incomeCategoryManagementService = incomeCategoryManagementService;
    }

    public AbstractCategoryResponse addCategory(CreateCategoryRequest request, User user, CategoryType categoryType) {
        ICategoryManagementService categoryManagementService = getService(categoryType);
        return categoryManagementService.addCategory(request, user);
    }

    public AbstractCategoryResponse setAsFavourite(long categoryId, CategoryIsFavouriteRequest request, User user, CategoryType categoryType) {
        ICategoryManagementService categoryManagementService = getService(categoryType);
        return categoryManagementService.setAsFavourite(categoryId, request.getIsFavourite(), user);
    }

    public AbstractCategoryResponse deleteCategory(long categoryId, User user, CategoryType categoryType) {
        ICategoryManagementService categoryManagementService = getService(categoryType);
        return categoryManagementService.deleteCategory(categoryId, user);
    }

    public CategoriesResponse getAllCategories(User user, CategoryType categoryType) {
        ICategoryManagementService categoryManagementService = getService(categoryType);
        return categoryManagementService.getAllCategories(user);
    }

    private ICategoryManagementService getService(CategoryType categoryType) {
        if (categoryType == null) {
            throw new IllegalArgumentException("Category is required.");
        }

        return switch (categoryType) {
            case EXPENSE -> expenseCategoryManagementService;
            case INCOME -> incomeCategoryManagementService;
        };
    }
}
