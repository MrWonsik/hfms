package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.finance.category.CategoryServiceType;
import com.wasacz.hfms.finance.category.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
class CategoryServiceFactory {

    private Map<CategoryServiceType, ICategoryService> categoryServices;

    CategoryServiceFactory(Set<ICategoryService> categoryServiceSet) {
        createCategoryService(categoryServiceSet);
    }

    ICategoryService getService(CategoryServiceType strategyName) {
        return categoryServices.get(strategyName);
    }
    private void createCategoryService(Set<ICategoryService> categoryServiceSet) {
        categoryServices = new HashMap<>();
        categoryServiceSet.forEach(
                categoryService -> categoryServices.put(getServiceType(categoryService), categoryService));
    }

    private CategoryServiceType getServiceType(ICategoryService categoryService) {
        String serviceName = categoryService.getServiceName();
        return switch (serviceName) {
            case "EXPENSE_CATEGORY_SERVICE" -> CategoryServiceType.EXPENSE;
            case "INCOME_CATEGORY_SERVICE" -> CategoryServiceType.INCOME;
            default -> throw new IllegalArgumentException("Invalid service name.");
        };
    }
}
