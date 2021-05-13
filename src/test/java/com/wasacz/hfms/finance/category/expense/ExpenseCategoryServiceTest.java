package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryVersionMapper;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryVersionResponse;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryServiceTest {

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private ExpenseCategoryVersionService expenseCategoryVersionService;

    @Mock
    private ExpenseCategorySaver expenseCategorySaver;

    @Mock
    private ExpenseCategoryVersionMapper expenseCategoryVersionMapper;

    @InjectMocks
    private ExpenseCategoryService expenseCategoryService;

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryResponse_thenSaveExpenseCategory() {
        //given
        ExpenseCategoryObj expenseCategoryObj = ExpenseCategoryObj.builder()
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
                .maximumAmount(BigDecimal.TEN)
                .validMonth(now)
                .build();
        ExpenseCategoryVersionResponse expenseCategoryVersionResponse = ExpenseCategoryVersionResponse
                .builder()
                .id(1L)
                .maximumAmount(BigDecimal.TEN.doubleValue())
                .validMonth(now)
                .isValid(true)
                .build();

        when(expenseCategorySaver.saveExpenseCategory(any(ExpenseCategoryObj.class), any(User.class))).thenReturn(expenseCategoryVersion);
        when(expenseCategoryVersionService.getCurrentCategoryVersion(any(Long.class))).thenReturn(expenseCategoryVersion);
        when(expenseCategoryVersionService.getCategoryVersions(any(Long.class))).thenReturn(List.of(expenseCategoryVersion));
        when(expenseCategoryVersionMapper.mapExpenseCategoryVersionToResponse(any(ExpenseCategoryVersion.class))).thenReturn(expenseCategoryVersionResponse);
        when(expenseCategoryVersionMapper.mapExpenseCategoryVersionsListToResponse(anyList())).thenReturn(List.of(expenseCategoryVersionResponse));

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.addCategory(expenseCategoryObj, user);

        //then
        assertEquals(expenseCategoryResponse.getCategoryName(), expenseCategoryObj.getCategoryName());
        assertFalse(expenseCategoryResponse.isDeleted());
        assertEquals(expenseCategoryResponse.getColorHex(), expenseCategoryObj.getColorHex());
        assertEquals(expenseCategoryResponse.isFavourite(), expenseCategoryObj.getIsFavourite());
        ExpenseCategoryVersionResponse currentVersion = expenseCategoryResponse.getCurrentVersion();
        assertEquals(currentVersion.getId(), expenseCategoryVersion.getId());
        assertEquals(currentVersion.getMaximumAmount(), expenseCategoryVersion.getMaximumAmount().doubleValue());
        assertEquals(currentVersion.getValidMonth(), expenseCategoryVersion.getValidMonth());
        assertTrue(currentVersion.isValid());
    }

    @Test
    public void whenSetAsFavouriteExpenseCategory_givenEditExpenseCategoryRequest_thenReturnEditedExpenseCategory() {
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
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.setAsFavourite(1L, true, user);

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
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.deleteCategory(1L, user);

        //then
        assertTrue(expenseCategoryResponse.isDeleted());
    }


    @Test
    public void whenSetAsFavouriteExpenseCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseCategoryService.setAsFavourite(1L, false, user));
        assertEquals(exception.getMessage(), "Expense category not found.");
    }

    @Test
    public void whenDeleteExpenseCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseCategoryService.deleteCategory(1L, user));
        assertEquals(exception.getMessage(), "Expense category not found.");
    }

    @Test
    public void whenEditExpenseCategory_givenEditCategoryRequest_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        ExpenseCategory expenseCategoryUpdated = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("CategoryName")
                .colorHex("#aaa")
                .user(user)
                .isDeleted(false)
                .build();
        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(expenseCategory));
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategoryUpdated);

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.editCategory(1L, "CategoryName", "#aaa", user);

        //then
        assertEquals(expenseCategoryUpdated.getCategoryName(), expenseCategoryResponse.getCategoryName());
        assertEquals(expenseCategoryUpdated.getColorHex(), expenseCategoryResponse.getColorHex());
    }

    @Test
    public void whenEditExpenseCategory_givenEditCategoryRequestWithNullParams_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();

        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(expenseCategory));

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.editCategory(1L, null, null, user);

        //then
        verify(expenseCategoryRepository, times(0)).save(any(ExpenseCategory.class));
        assertEquals(expenseCategory.getCategoryName(), expenseCategoryResponse.getCategoryName());
        assertEquals(expenseCategory.getColorHex(), expenseCategoryResponse.getColorHex());
    }

    @Test
    public void whenEditExpenseCategory_givenEditCategoryRequestWithoutCategoryName_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        ExpenseCategory expenseCategoryUpdated = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#aaa")
                .user(user)
                .isDeleted(false)
                .build();
        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(expenseCategory));
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategoryUpdated);

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.editCategory(1L, null, "#aaa", user);

        //then
        assertEquals(expenseCategory.getCategoryName(), expenseCategoryResponse.getCategoryName());
        assertEquals(expenseCategoryUpdated.getCategoryName(), expenseCategoryResponse.getCategoryName());
        assertEquals(expenseCategoryUpdated.getColorHex(), expenseCategoryResponse.getColorHex());
    }


    @Test
    public void whenEditExpenseCategory_givenEditCategoryRequestWithoutHexColor_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        ExpenseCategory expenseCategory = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        ExpenseCategory expenseCategoryUpdated = ExpenseCategory
                .builder()
                .id(1L)
                .categoryName("CategoryName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        when(expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(expenseCategory));
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(expenseCategoryUpdated);

        //when
        ExpenseCategoryResponse expenseCategoryResponse = expenseCategoryService.editCategory(1L, "CategoryName", null, user);

        //then
        assertEquals(expenseCategory.getColorHex(), expenseCategoryResponse.getColorHex());
        assertEquals(expenseCategoryUpdated.getCategoryName(), expenseCategoryResponse.getCategoryName());
        assertEquals(expenseCategoryUpdated.getColorHex(), expenseCategoryResponse.getColorHex());
    }

    @Test
    public void whenEditExpenseCategory_givenEditCategoryRequestWithIncorrectCategoryName_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        //then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> expenseCategoryService.editCategory(1L, "", null, user));
        assertEquals(exception.getMessage(), "categoryName cannot be blank.");
    }

    @Test
    public void whenEditExpenseCategory_givenEditCategoryRequestWithIncorrectHexColor_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseCategoryService.editCategory(1L, null, "#aaaaaa00", user));
        assertEquals(exception.getMessage(), "Incorrect hex color provided.");
    }
}