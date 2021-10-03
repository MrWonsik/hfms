package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.finance.category.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.ExpenseCategoryService;
import com.wasacz.hfms.finance.category.IncomeCategoryObj;
import com.wasacz.hfms.finance.category.IncomeCategoryService;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryStarterFactory {

    private ExpenseCategoryService expenseCategoryService;
    private IncomeCategoryService incomeCategoryService;

    private final List<ExpenseCategoryObj> expenseStarterCategories = createExpenseStarterCategories();
    private final List<IncomeCategoryObj> incomeStarterCategories = createIncomeStarterCategories();

    public CategoryStarterFactory(ExpenseCategoryService expenseCategoryService, IncomeCategoryService incomeCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
        this.incomeCategoryService = incomeCategoryService;
    }

    private List<IncomeCategoryObj> createIncomeStarterCategories() {
        return List.of(
                IncomeCategoryObj.builder().categoryName("Salary").build(),
                IncomeCategoryObj.builder().categoryName("Interest").build(),
                IncomeCategoryObj.builder().categoryName("Selling").build(),
                IncomeCategoryObj.builder().categoryName("Investments").build(),
                IncomeCategoryObj.builder().categoryName("Gifts").build(),
                IncomeCategoryObj.builder().categoryName("Other").build()
        );
    }

    private List<ExpenseCategoryObj> createExpenseStarterCategories() {
        return List.of(
            ExpenseCategoryObj.builder().categoryName("Grocery Shopping").build(),
            ExpenseCategoryObj.builder().categoryName("Transport").build(),
            ExpenseCategoryObj.builder().categoryName("Car").build(),
            ExpenseCategoryObj.builder().categoryName("Bills").build(),
            ExpenseCategoryObj.builder().categoryName("Culture and entertainment").build(),
            ExpenseCategoryObj.builder().categoryName("Health and beauty").build(),
            ExpenseCategoryObj.builder().categoryName("Personal development").build(),
            ExpenseCategoryObj.builder().categoryName("Home").build(),
            ExpenseCategoryObj.builder().categoryName("Clothes and accessories").build(),
            ExpenseCategoryObj.builder().categoryName("Other").build()
        );
    }

    public void addStarterCategoriesForUser(User user) {
        expenseStarterCategories.forEach(expenseCategoryObj -> expenseCategoryService.addCategory(expenseCategoryObj, user));
        incomeStarterCategories.forEach(incomeCategoryObj -> incomeCategoryService.addCategory(incomeCategoryObj, user));
    }
}
