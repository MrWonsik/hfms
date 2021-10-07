package com.wasacz.hfms.finance.category.controller;

import com.wasacz.hfms.finance.ServiceType;
import com.wasacz.hfms.finance.category.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
public class CategoryServiceFactory {

    private Map<ServiceType, ICategoryService> categoryServices;

    CategoryServiceFactory(Set<ICategoryService> categoryServiceSet) {
        createCategoryService(categoryServiceSet);
    }

    public ICategoryService getService(ServiceType strategyName) {
        return categoryServices.get(strategyName);
    }
    private void createCategoryService(Set<ICategoryService> categoryServiceSet) {
        categoryServices = new HashMap<>();
        categoryServiceSet.forEach(
                categoryService -> categoryServices.put(getServiceType(categoryService), categoryService));
    }

    private ServiceType getServiceType(ICategoryService categoryService) {
        String serviceName = categoryService.getServiceName();
        return switch (serviceName) {
            case "EXPENSE_CATEGORY_SERVICE" -> ServiceType.EXPENSE;
            case "INCOME_CATEGORY_SERVICE" -> ServiceType.INCOME;
            default -> throw new IllegalArgumentException("Invalid service name.");
        };
    }
}
