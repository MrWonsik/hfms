package com.wasacz.hfms.finance.category.income;

import com.wasacz.hfms.finance.category.TransactionSummaryProvider;
import com.wasacz.hfms.finance.transaction.TransactionType;
import com.wasacz.hfms.persistence.IncomeCategory;
import com.wasacz.hfms.persistence.IncomeCategoryRepository;
import com.wasacz.hfms.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeCategoryServiceTest {

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @Mock
    private TransactionSummaryProvider transactionSummaryProvider;

    @InjectMocks
    private IncomeCategoryService incomeCategoryService;

    @Test
    public void whenAddIncomeCategory_givenCreateCategoryResponse_thenSaveExpenseCategory() {
        //given
        IncomeCategoryObj categoryObj = IncomeCategoryObj.builder()
                .categoryName("Work")
                .colorHex("#F00")
                .isFavourite(false)
                .build();
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("Work")
                .colorHex("#F00")
                .isFavourite(false)
                .user(user)
                .isDeleted(false)
                .build();

        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(incomeCategory);
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.addCategory(categoryObj, user);

        //then
        assertEquals(incomeCategoryResponse.getCategoryName(), categoryObj.getCategoryName());
        assertFalse(incomeCategoryResponse.isDeleted());
        assertEquals(incomeCategoryResponse.getColorHex(), categoryObj.getColorHex());
        assertEquals(incomeCategoryResponse.isFavourite(), categoryObj.getIsFavourite());
    }

    @Test
    public void whenSetAsFavouriteIncomeCategory_givenIsFavouriteCategoryRequest_thenReturnEditedIncomeCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(true)
                .user(user)
                .isDeleted(false)
                .build();
        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(incomeCategory));
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(incomeCategory);
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.toggleFavourite(1L, true, user);

        //then
        assertTrue(incomeCategoryResponse.isFavourite());
        assertFalse(incomeCategoryResponse.isDeleted());
    }

    @Test
    public void whenDeleteIncomeCategory_thenReturnDeletedIncomeCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(true)
                .user(user)
                .isDeleted(false)
                .build();
        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(incomeCategory));
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(incomeCategory);
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.deleteCategory(1L, user);

        //then
        assertTrue(incomeCategoryResponse.isDeleted());
    }


    @Test
    public void whenSetAsFavouriteIncomeCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> incomeCategoryService.toggleFavourite(1L, false, user));
        assertEquals(exception.getMessage(), "Income category not found.");
    }

    @Test
    public void whenDeleteIncomeCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> incomeCategoryService.deleteCategory(1L, user));
        assertEquals(exception.getMessage(), "Income category not found.");
    }


    @Test
    public void whenEditIncomeCategory_givenEditCategoryRequest_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        IncomeCategory incomeCategoryUpdated = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("CategoryName")
                .colorHex("#aaa")
                .user(user)
                .isDeleted(false)
                .build();
        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(incomeCategory));
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(incomeCategoryUpdated);
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.editCategory(1L, "CategoryName", "#aaa", user);

        //then
        assertEquals(incomeCategoryUpdated.getCategoryName(), incomeCategoryResponse.getCategoryName());
        assertEquals(incomeCategoryUpdated.getColorHex(), incomeCategoryResponse.getColorHex());
    }

    @Test
    public void whenEditIncomeCategory_givenEditCategoryRequestWithNullParams_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();

        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(incomeCategory));
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.editCategory(1L, null, null, user);

        //then
        verify(incomeCategoryRepository, times(0)).save(any(IncomeCategory.class));
        assertEquals(incomeCategory.getCategoryName(), incomeCategoryResponse.getCategoryName());
        assertEquals(incomeCategory.getColorHex(), incomeCategoryResponse.getColorHex());
    }

    @Test
    public void whenEditIncomeCategory_givenEditCategoryRequestWithoutCategoryName_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        IncomeCategory incomeCategoryUpdated = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#aaa")
                .user(user)
                .isDeleted(false)
                .build();
        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(incomeCategory));
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(incomeCategoryUpdated);
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.editCategory(1L, null, "#aaa", user);

        //then
        assertEquals(incomeCategory.getCategoryName(), incomeCategoryResponse.getCategoryName());
        assertEquals(incomeCategoryUpdated.getCategoryName(), incomeCategoryResponse.getCategoryName());
        assertEquals(incomeCategoryUpdated.getColorHex(), incomeCategoryResponse.getColorHex());
    }


    @Test
    public void whenEditIncomeCategory_givenEditCategoryRequestWithoutHexColor_thenEditCategory() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        IncomeCategory incomeCategory = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("someName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        IncomeCategory incomeCategoryUpdated = IncomeCategory
                .builder()
                .id(1L)
                .categoryName("CategoryName")
                .colorHex("#bbb")
                .user(user)
                .isDeleted(false)
                .build();
        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.of(incomeCategory));
        when(incomeCategoryRepository.save(any(IncomeCategory.class))).thenReturn(incomeCategoryUpdated);
        when(transactionSummaryProvider.getTransactionMapProvider(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyMap());

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryService.editCategory(1L, "CategoryName", null, user);

        //then
        assertEquals(incomeCategory.getColorHex(), incomeCategoryResponse.getColorHex());
        assertEquals(incomeCategoryUpdated.getCategoryName(), incomeCategoryResponse.getCategoryName());
        assertEquals(incomeCategoryUpdated.getColorHex(), incomeCategoryResponse.getColorHex());
    }

    @Test
    public void whenEditIncomeCategory_givenEditCategoryRequestWithIncorrectCategoryName_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        //then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> incomeCategoryService.editCategory(1L, "", null, user));
        assertEquals(exception.getMessage(), "categoryName cannot be blank.");
    }

    @Test
    public void whenEditIncomeCategory_givenEditCategoryRequestWithIncorrectHexColor_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> incomeCategoryService.editCategory(1L, null, "#aaaaaa00", user));
        assertEquals(exception.getMessage(), "Incorrect hex color provided.");
    }
}