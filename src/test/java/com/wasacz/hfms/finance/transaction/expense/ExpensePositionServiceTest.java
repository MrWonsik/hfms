package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.persistence.ExpensePositionRepository;
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

}