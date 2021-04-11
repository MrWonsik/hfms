package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.controller.CreateCategoryRequest;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryVersionService;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryManagementService;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryVersionResponse;
import com.wasacz.hfms.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryManagementServiceTest {

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private ExpenseCategoryVersionRepository expenseCategoryVersionRepository;

    @Mock
    private ExpenseCategoryVersionService expenseCategoryVersionService;

    @InjectMocks
    private ExpenseCategoryManagementService expenseCategoryManagementService;

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryResponse_thenSaveExpenseCategory() {
        //given
        CreateCategoryRequest expenseCategoryObj = CreateCategoryRequest.builder()
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

        YearMonth now = YearMonth.now();
        ExpenseCategoryVersion expenseCategoryVersion = ExpenseCategoryVersion
                .builder()
                .id(1L)
                .expenseCategory(expenseCategory)
                .maximumCost(BigDecimal.TEN)
                .validMonth(now)
                .build();
        ExpenseCategoryVersionResponse expenseCategoryVersionResponse = ExpenseCategoryVersionResponse
                .builder()
                .id(1L)
                .maximumCost(BigDecimal.TEN.doubleValue())
                .validMonth(now)
                .isValid(true)
                .build();

        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategory);
        when(expenseCategoryVersionService.saveCategory(any(ExpenseCategoryObj.class), any(ExpenseCategory.class))).thenReturn(expenseCategoryVersion);
        when(expenseCategoryVersionService.getNewestCategoryVersion(any(ExpenseCategory.class))).thenReturn(expenseCategoryVersionResponse);
        when(expenseCategoryVersionService.getCategoryVersions(any(ExpenseCategory.class))).thenReturn(List.of(expenseCategoryVersionResponse));

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.addCategory(expenseCategoryObj, user);

        //then
        assertEquals(expenseCategoryResponse.getCategoryName(), expenseCategoryObj.getCategoryName());
        assertFalse(expenseCategoryResponse.isDeleted());
        assertEquals(expenseCategoryResponse.getColorHex(), expenseCategoryObj.getColorHex());
        assertEquals(expenseCategoryResponse.isFavourite(), expenseCategoryObj.getIsFavourite());
        ExpenseCategoryVersionResponse currentVersion = expenseCategoryResponse.getCurrentVersion();
        assertEquals(currentVersion.getId(), expenseCategoryVersionResponse.getId());
        assertEquals(currentVersion.getMaximumCost(), expenseCategoryVersionResponse.getMaximumCost());
        assertEquals(currentVersion.getValidMonth(), expenseCategoryVersionResponse.getValidMonth());
        assertTrue(currentVersion.isValid());
    }

    @Test
    public void whenEditExpenseCategory_givenEditExpenseCategoryRequest_thenReturnEditedExpenseCategory() {
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
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.setAsFavourite(1L, true, user);

        //then
        assertTrue(expenseCategoryResponse.isFavourite());
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
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryManagementService.deleteCategory(1L, user);

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
                () -> expenseCategoryManagementService.setAsFavourite(1L, false, user));
        assertEquals(exception.getMessage(), "Expense category not found.");
    }

    @Test
    public void whenDeleteExpenseCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseCategoryManagementService.deleteCategory(1L, user));
        assertEquals(exception.getMessage(), "Expense category not found.");
    }
}