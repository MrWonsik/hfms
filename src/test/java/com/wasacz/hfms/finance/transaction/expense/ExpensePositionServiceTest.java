package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.persistence.ExpensePositionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensePositionServiceTest {

    @Mock
    private ExpensePositionRepository repository;

    @InjectMocks
    private ExpensePositionService expensePositionService;

    @Test
    public void whenUpdateExpensePositions_givenNewExpensePositionEmptyList_thenSaveNewExpensePosition() {
        //given
        Expense expense = mock(Expense.class);

        List<ExpensePosition> oldExpenses = new ArrayList<>();
        oldExpenses.add(ExpensePosition.builder().id(1L).expensePositionName("Position1").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());
        oldExpenses.add(ExpensePosition.builder().id(2L).expensePositionName("Position2").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());
        oldExpenses.add(ExpensePosition.builder().id(3L).expensePositionName("Position3").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());

        when(expense.getId()).thenReturn(1L);
        when(repository.findAllByExpenseId(1L)).thenReturn(Optional.of(oldExpenses));

        //when
        expensePositionService.updateExpensePositions(expense, Collections.emptyList());

        //then
        verify(repository, times(3)).delete(any());
    }

    @Test
    public void whenUpdateExpensePositions_givenIncorrectExpense_thenThrowException() {
        //then and then
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> expensePositionService.updateExpensePositions(null, Collections.emptyList()));
        assertEquals("Expense cannot be null!", exception.getMessage());
    }

    @Test
    public void whenAddExpensePositions_givenIncorrectExpense_thenThrowException() {
        //then and then
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> expensePositionService.addExpensePositions(null, Collections.emptyList()));
        assertEquals("Expense cannot be null!", exception.getMessage());
    }

    @Test
    public void whenUpdateExpensePositions_givenNewExpensePositionList_thenSaveNewExpensePosition() {
        //given
        Expense expense = mock(Expense.class);

        List<ExpensePosition> oldExpenses = new ArrayList<>();
        oldExpenses.add(ExpensePosition.builder().id(1L).expensePositionName("Position1").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());
        oldExpenses.add(ExpensePosition.builder().id(2L).expensePositionName("Position2").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());

        List<ExpensePositionObj> newExpenses = new ArrayList<>();
        newExpenses.add(ExpensePositionObj.builder().id(1L).positionName("Position1_edit").size(1d).cost(123d).build());
        newExpenses.add(ExpensePositionObj.builder().id(null).positionName("Position2").size(1d).cost(123d).build());

        ExpensePosition newExpensePosition = ExpensePosition.builder().id(1L).expensePositionName("Position1").size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(122d)).build();

        when(expense.getId()).thenReturn(1L);
        when(repository.findAllByExpenseId(1L)).thenReturn(Optional.of(oldExpenses));
        when(repository.findById(1L)).thenReturn(Optional.of(newExpensePosition));
        when(repository.save(any())).then(returnsFirstArg());

        //when
        List<ExpensePosition> expensePositionList = expensePositionService.updateExpensePositions(expense, newExpenses);

        //then
        verify(repository, times(1)).delete(any());
        verify(repository, times(2)).save(any());
        assertEquals(2, expensePositionList.size());
        assertEquals(BigDecimal.valueOf(123.0), expensePositionList.get(0).getCost());
        assertEquals(BigDecimal.valueOf(1.0), expensePositionList.get(0).getSize());
        assertEquals("Position1_edit", expensePositionList.get(0).getExpensePositionName());
        assertEquals(BigDecimal.valueOf(123.0), expensePositionList.get(1).getCost());
        assertEquals(BigDecimal.valueOf(1.0), expensePositionList.get(1).getSize());
        assertEquals("Position2", expensePositionList.get(1).getExpensePositionName());
    }

    @Test
    public void whenUpdateExpensePositions_givenIncorrectNewExpensePositionList_thenThrowException() {
        //given
        Expense expense = mock(Expense.class);

        List<ExpensePosition> oldExpenses = new ArrayList<>();
        oldExpenses.add(ExpensePosition.builder().id(1L).expensePositionName("Position1").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());
        oldExpenses.add(ExpensePosition.builder().id(2L).expensePositionName("Position2").expense(expense).size(BigDecimal.valueOf(1)).cost(BigDecimal.valueOf(123)).build());

        List<ExpensePositionObj> newExpenses = new ArrayList<>();
        newExpenses.add(ExpensePositionObj.builder().id(1L).positionName("Position1_edit").size(1d).cost(123d).build());
        newExpenses.add(ExpensePositionObj.builder().id(null).positionName("Position2").size(1d).cost(123d).build());

        when(expense.getId()).thenReturn(1L);
        when(expense.getExpenseName()).thenReturn("test_expense_name");
        when(repository.findAllByExpenseId(1L)).thenReturn(Optional.of(oldExpenses));
        when(repository.findById(1L)).thenReturn(Optional.empty());

        //when
        IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> expensePositionService.updateExpensePositions(expense, newExpenses));
        assertEquals("Provide incorrect position id: 1 for expense: test_expense_name, position with this id not exists.", exception.getMessage());
        //then

    }

}