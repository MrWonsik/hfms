package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.persistence.ExpensePositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpensePositionService {

    private final ExpensePositionRepository expensePositionRepository;

    public ExpensePositionService(ExpensePositionRepository expensePositionRepository) {
        this.expensePositionRepository = expensePositionRepository;
    }

    public List<ExpensePosition> addExpensePositions(Expense expense, List<ExpensePositionObj> expensePositions) {
        if(expense == null) {
            throw new IllegalStateException("Incorrect expense.");
        }
        if(expensePositions == null || expensePositions.isEmpty()) {
            return Collections.emptyList();
        }
        return expensePositions
                .stream()
                .map(expensePositionObj -> saveExpensePosition(expense, expensePositionObj))
                .collect(Collectors.toList());
    }

    public Optional<List<ExpensePosition>> getExpensePositionList(Long expenseId) {
        return expensePositionRepository.findAllByExpenseId(expenseId);
    }

    private ExpensePosition saveExpensePosition(Expense expense, ExpensePositionObj expensePositionObj) {
        ExpenseValidator.validateExpensePosition(expensePositionObj);
        return expensePositionRepository.save(ExpensePosition.builder()
                .expense(expense)
                .expensePositionName(expensePositionObj.getPositionName())
                .cost(BigDecimal.valueOf(expensePositionObj.getCost()))
                .size(BigDecimal.valueOf(expensePositionObj.getSize()))
                .build());
    }
}
