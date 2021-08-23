package com.wasacz.hfms.finance.category;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorTest {

    @Test
    public void whenValidateExpenseCategoryRequest_givenCorrectData_thenDoNotThrowException() {
        //given
        ExpenseCategoryObj expenseCategoryObj = ExpenseCategoryObj.builder()
                .categoryName("CategoryName")
                .colorHex("#123123")
                .isFavourite(true)
                .build();

        //when and then
        Assertions.assertDoesNotThrow(() -> CategoryValidator.validate(expenseCategoryObj));
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenCorrectDataWithoutHexString_thenDoNotThrowException() {
        //given
        ExpenseCategoryObj expenseCategoryObj = ExpenseCategoryObj.builder()
                .categoryName("CategoryName")
                .isFavourite(true)
                .build();

        //when and then
        Assertions.assertDoesNotThrow(() -> CategoryValidator.validate(expenseCategoryObj));
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenCorrectIncorrectHexString_thenThrowException() {
        //given
        ExpenseCategoryObj expenseCategoryObj = ExpenseCategoryObj.builder()
                .categoryName("CategoryName")
                .colorHex("#1")
                .isFavourite(true)
                .build();

        //when and then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CategoryValidator.validate(expenseCategoryObj));
        assertEquals("Incorrect hex color provided.", exception.getMessage());
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenEmptyCategoryName_thenThrowException() {
        //given
        ExpenseCategoryObj expenseCategoryObj = ExpenseCategoryObj.builder()
                .categoryName("")
                .build();

        //when and then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> CategoryValidator.validate(expenseCategoryObj));
        assertEquals("Field: categoryName cannot be blank.", exception.getMessage());
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenNegativeMaximumAmount_thenThrowException() {
        //given
        ExpenseCategoryObj expenseCategoryObj = ExpenseCategoryObj.builder()
                .categoryName("Negative maximum amount")
                .maximumAmount(BigDecimal.valueOf(-5))
                .build();

        //when and then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CategoryValidator.validate(expenseCategoryObj));
        assertEquals("Maximum amount should be grater than 0.", exception.getMessage());
    }

}