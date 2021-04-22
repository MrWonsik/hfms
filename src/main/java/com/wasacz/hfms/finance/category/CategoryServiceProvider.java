package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.controller.AbstractCategoryResponse;
import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.controller.CategoryIsFavouriteRequest;
import com.wasacz.hfms.finance.category.controller.CategoryObj;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceProvider {

    private final ICategoryManagementService expenseCategoryService;
    private final ICategoryManagementService incomeCategoryService;

    public CategoryServiceProvider(ICategoryManagementService expenseCategoryService, ICategoryManagementService incomeCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
        this.incomeCategoryService = incomeCategoryService;
    }

    public AbstractCategoryResponse addCategory(CategoryObj request, User user, CategoryType categoryType) {
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
            case EXPENSE -> expenseCategoryService;
            case INCOME -> incomeCategoryService;
        };
    }

    public AbstractCategoryResponse editCategory(long id, String newCategoryName, String newColorHex, User user, CategoryType categoryType) {
        ICategoryManagementService categoryManagementService = getService(categoryType);
        return categoryManagementService.editCategory(id, newCategoryName, newColorHex, user);
    }
}
