package com.wasacz.hfms.finance.category.expense;

import com.wasacz.hfms.persistence.ExpenseCategory;
import com.wasacz.hfms.persistence.ExpenseCategoryVersion;
import com.wasacz.hfms.persistence.ExpenseCategoryVersionRepository;
import com.wasacz.hfms.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryVersionServiceTest {

    @InjectMocks
    private ExpenseCategoryVersionService service;

    @Mock
    private ExpenseCategoryVersionRepository repository;

    @Test
    public void whenGetCurrentCategoryVersion_givenExpenseCategory_thenReturnCurrentVersion() {
        //given
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


        ExpenseCategoryVersion currentVersion = ExpenseCategoryVersion
                .builder()
                .id(1L)
                .expenseCategory(expenseCategory)
                .maximumAmount(BigDecimal.TEN)
                .validMonth(YearMonth.now())
                .build();

        ExpenseCategoryVersion nextMonth = ExpenseCategoryVersion
                .builder()
                .id(2L)
                .expenseCategory(expenseCategory)
                .maximumAmount(BigDecimal.TEN)
                .validMonth(YearMonth.now().plusMonths(1))
                .build();

        ExpenseCategoryVersion previousMonth = ExpenseCategoryVersion
                .builder()
                .id(3L)
                .expenseCategory(expenseCategory)
                .maximumAmount(BigDecimal.TEN)
                .validMonth(YearMonth.now().minusMonths(1))
                .build();

        when(repository.findByExpenseCategoryId(any(Long.class))).thenReturn(Optional.of(List.of(previousMonth, currentVersion, nextMonth)));

        //when
        ExpenseCategoryVersion currentCategoryVersion = service.getCurrentCategoryVersion(1L);

        //then
        assertEquals(currentVersion.getValidMonth(), currentCategoryVersion.getValidMonth());
        assertEquals(currentVersion.getExpenseCategory(), currentCategoryVersion.getExpenseCategory());
        assertEquals(currentVersion.getId(), currentCategoryVersion.getId());
    }

    @Test
    public void whenGetCurrentCategoryVersion_givenExpenseCategoryWithNoVersionForValidMonth_thenReturnCurrentVersion() {
        //given
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

        ExpenseCategoryVersion nextMonth = ExpenseCategoryVersion
                .builder()
                .id(2L)
                .expenseCategory(expenseCategory)
                .maximumAmount(BigDecimal.TEN)
                .validMonth(YearMonth.now().plusMonths(1))
                .build();

        ExpenseCategoryVersion previousVersion = ExpenseCategoryVersion
                .builder()
                .id(3L)
                .expenseCategory(expenseCategory)
                .maximumAmount(BigDecimal.TEN)
                .validMonth(YearMonth.now().minusMonths(3))
                .build();

        when(repository.findByExpenseCategoryId(any(Long.class))).thenReturn(Optional.of(List.of(previousVersion, nextMonth)));

        //when
        ExpenseCategoryVersion currentCategoryVersion = service.getCurrentCategoryVersion(1L);

        //then
        assertEquals(previousVersion.getValidMonth(), currentCategoryVersion.getValidMonth());
        assertEquals(previousVersion.getExpenseCategory(), currentCategoryVersion.getExpenseCategory());
        assertEquals(previousVersion.getId(), currentCategoryVersion.getId());
    }

    @Test //CornerCase!!!
    public void whenGetCurrentCategoryVersion_givenExpenseCategoryWihtoutVersion_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(repository.findByExpenseCategoryId(any(Long.class))).thenReturn(Optional.empty());

        //when and then
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> service.getCurrentCategoryVersion(1L));
        assertEquals("Expense category versions is empty!", illegalStateException.getMessage());
    }

    @Test //CornerCase!!!
    public void whenGetCurrentCategoryVersion_givenExpenseCategoryOnlyWithNextMonthVersion_thenThrowException() {
        //given
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

        ExpenseCategoryVersion nextMonth = ExpenseCategoryVersion
                .builder()
                .id(2L)
                .expenseCategory(expenseCategory)
                .maximumAmount(BigDecimal.TEN)
                .validMonth(YearMonth.now().plusMonths(1))
                .build();


        when(repository.findByExpenseCategoryId(any(Long.class))).thenReturn(Optional.of(List.of(nextMonth)));

        //when and then
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> service.getCurrentCategoryVersion(1L));
        assertEquals("Not found current version!", illegalStateException.getMessage());
    }

}