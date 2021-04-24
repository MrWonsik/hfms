package com.wasacz.hfms.finance.category;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
public class CategoryServiceFactory {

    private Map<CategoryType, ICategoryService> categoryServices;

    public CategoryServiceFactory(Set<ICategoryService> categoryServiceSet) {
        createCategoryService(categoryServiceSet);
    }

    public ICategoryService getService(CategoryType strategyName) {
        return categoryServices.get(strategyName);
    }
    private void createCategoryService(Set<ICategoryService> categoryServiceSet) {
        categoryServices = new HashMap<>();
        categoryServiceSet.forEach(
                categoryService -> categoryServices.put(categoryService.getService(), categoryService));
    }
}
