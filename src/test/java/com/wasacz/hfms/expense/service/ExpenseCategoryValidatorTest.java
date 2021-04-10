package com.wasacz.hfms.expense.service;

import com.wasacz.hfms.expense.controller.CreateExpenseCategoryRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryValidatorTest {

    @Test
    public void whenValidateExpenseCategoryRequest_givenCorrectData_thenDoNotThrowException() {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest.builder()
                .categoryName("CategoryName")
                .colorHex("#123123")
                .isFavourite(true)
                .build();

        //when and then
        Assertions.assertDoesNotThrow(() -> ExpenseCategoryValidator.validate(createExpenseCategoryRequest));
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenCorrectDataWithoutHexString_thenDoNotThrowException() {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest.builder()
                .categoryName("CategoryName")
                .isFavourite(true)
                .build();

        //when and then
        Assertions.assertDoesNotThrow(() -> ExpenseCategoryValidator.validate(createExpenseCategoryRequest));
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenCorrectIncorrectHexString_thenDoNotThrowException() {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest.builder()
                .categoryName("CategoryName")
                .colorHex("#1")
                .isFavourite(true)
                .build();

        //when and then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ExpenseCategoryValidator.validate(createExpenseCategoryRequest));
        assertEquals("Incorrect hex color provided.", exception.getMessage());
    }

    @Test
    public void whenValidateExpenseCategoryRequest_givenEmptyCategoryName_thenDoNotThrowException() {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest.builder()
                .categoryName("")
                .build();

        //when and then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ExpenseCategoryValidator.validate(createExpenseCategoryRequest));
        assertEquals("categoryName cannot be blank.", exception.getMessage());
    }

}