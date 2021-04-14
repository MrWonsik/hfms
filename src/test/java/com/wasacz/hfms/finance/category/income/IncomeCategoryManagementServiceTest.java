package com.wasacz.hfms.finance.category.income;

import com.wasacz.hfms.finance.category.controller.CreateCategoryRequest;
import com.wasacz.hfms.finance.category.income.IncomeCategoryManagementService;
import com.wasacz.hfms.finance.category.income.IncomeCategoryResponse;
import com.wasacz.hfms.persistence.IncomeCategory;
import com.wasacz.hfms.persistence.IncomeCategoryRepository;
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
class IncomeCategoryManagementServiceTest {

    @Mock
    private IncomeCategoryRepository incomeCategoryRepository;

    @InjectMocks
    private IncomeCategoryManagementService incomeCategoryManagementService;

    @Test
    public void whenAddIncomeCategory_givenCreateCategoryResponse_thenSaveExpenseCategory() {
        //given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
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

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryManagementService.addCategory(createCategoryRequest, user);

        //then
        assertEquals(incomeCategoryResponse.getCategoryName(), createCategoryRequest.getCategoryName());
        assertFalse(incomeCategoryResponse.isDeleted());
        assertEquals(incomeCategoryResponse.getColorHex(), createCategoryRequest.getColorHex());
        assertEquals(incomeCategoryResponse.isFavourite(), createCategoryRequest.getIsFavourite());
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

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryManagementService.setAsFavourite(1L, true, user);

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

        //when
        IncomeCategoryResponse incomeCategoryResponse = incomeCategoryManagementService.deleteCategory(1L, user);

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
                () -> incomeCategoryManagementService.setAsFavourite(1L, false, user));
        assertEquals(exception.getMessage(), "Income category not found.");
    }

    @Test
    public void whenDeleteIncomeCategory_givenIdThatDontExists_thenThrowException() {
        //given
        User user = User.builder().id(1L).username("Test").build();

        when(incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(1L, user)).thenReturn(Optional.empty());

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> incomeCategoryManagementService.deleteCategory(1L, user));
        assertEquals(exception.getMessage(), "Income category not found.");
    }
}