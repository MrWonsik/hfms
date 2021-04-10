package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.CreateExpenseCategoryRequest;
import com.wasacz.hfms.expense.controller.EditExpenseCategoryRequest;
import com.wasacz.hfms.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpenseCategoryRepository;
import com.wasacz.hfms.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @Test
    public void whenEditExpenseCategory_givenEditExpenseCategoryRequest_thenReturnEditedExpenseCategory() {
        //given
        EditExpenseCategoryRequest editExpenseCategoryRequest = new EditExpenseCategoryRequest();
        editExpenseCategoryRequest.setIsFavourite(true);
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(true)
                .user(user)
                .isDeleted(false)
                .build();
        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(expenseCategory));
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategory);

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.editExpenseCategory(1L, editExpenseCategoryRequest, user);

        //then
        assertEquals(expenseCategoryResponse.isFavourite(), editExpenseCategoryRequest.getIsFavourite());
        assertFalse(expenseCategoryResponse.isDeleted());
    }

    @Test
    public void whenDeleteExpenseCategory_thenReturnDeletedExpenseCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(true)
                .user(user)
                .isDeleted(false)
                .build();
        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(expenseCategory));
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategory);

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.deleteExpenseCategory(1L, user);

        //then
        assertTrue(expenseCategoryResponse.isDeleted());
    }


    @Test
    public void whenEditExpenseCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseCategoryManagementService.editExpenseCategory(1L, null, user));
        assertEquals(exception.getMessage(), "Expense category not found.");
    }

    @Test
    public void whenDeleteExpenseCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseCategoryManagementService.deleteExpenseCategory(1L, user));
        assertEquals(exception.getMessage(), "Expense category not found.");
    }
}