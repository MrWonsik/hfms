package com.wasacz.hfms.expense.controller;

import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpenseCategoryRepository;
import com.wasacz.hfms.persistence.User;
import org.springframework.stereotype.Service;

@Service
public class ExpenseCategoryManagementService {
    //TODO: add, delete, edit, getAll

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseCategoryManagementService(ExpenseCategoryRepository expenseCategoryRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public ExpenseCategoryResponse addExpenseCategory(CreateExpenseCategoryRequest createExpenseCategoryRequest, User user) {
        ExpenseCategory expenseCategory = new ExpenseCategory();
        ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
        return mapExpenseCategoryToResponse(savedExpenseCategory);
    }

    private ExpenseCategoryResponse mapExpenseCategoryToResponse(ExpenseCategory savedExpenseCategory) {
        return ExpenseCategoryResponse.builder().build();
    }

}
