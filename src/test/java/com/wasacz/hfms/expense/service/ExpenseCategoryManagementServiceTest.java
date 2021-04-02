package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.CreateExpenseCategoryRequest;
import com.wasacz.hfms.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpenseCategoryRepository;
import com.wasacz.hfms.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryManagementServiceTest {

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @InjectMocks
    private ExpenseCategoryManagementService expenseCategoryManagementService;

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryResponse_thenSaveExpenseCategory() {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest
                .builder()
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(false)
                .build();
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(false)
                .user(user)
                .isDeleted(false)
                .build();
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategory);

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.addExpenseCategory(createExpenseCategoryRequest, user);

        //then
        assertEquals(expenseCategoryResponse.getCategoryName(), createExpenseCategoryRequest.getCategoryName());
        assertFalse(expenseCategoryResponse.isDeleted());
        assertEquals(expenseCategoryResponse.getColorHex(), createExpenseCategoryRequest.getColorHex());
        assertEquals(expenseCategoryResponse.isFavourite(), createExpenseCategoryRequest.getIsFavourite());
    }
}