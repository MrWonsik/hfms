package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.persistence.ExpensePositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
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
            throw new IllegalStateException("Expense cannot be null!");
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

    public List<ExpensePosition> updateExpensePositions(Expense updatedExpense, List<ExpensePositionObj> newExpensePositions) {
        if(newExpensePositions == null) {
            return Collections.emptyList();
        }
        if(updatedExpense == null) {
            throw new IllegalStateException("Expense cannot be null!");
        }
        List<ExpensePosition> oldExpensePositionList = getExpensePositionList(updatedExpense.getId()).orElse(Collections.emptyList());
        deleteOldPositionsLists(newExpensePositions, oldExpensePositionList);
        return newExpensePositions
                .stream()
                .map(expensePositionObj -> updateExpensePosition(updatedExpense, expensePositionObj))
                .collect(Collectors.toList());
    }

    private void deleteOldPositionsLists(List<ExpensePositionObj> newExpensePositions, List<ExpensePosition> oldExpensePositionList) {
        oldExpensePositionList.forEach(oldExpense -> {
            if(newExpensePositions.stream().noneMatch(newExpense -> newExpense.getId() != null && newExpense.getId().equals(oldExpense.getId()))) {
                expensePositionRepository.delete(oldExpense);
            }
        });
    }

    private ExpensePosition updateExpensePosition(Expense expense, ExpensePositionObj expensePositionObj) {
        if(expensePositionObj.getId() == null) {
            return saveExpensePosition(expense, expensePositionObj);
        }
        ExpenseValidator.validateExpensePosition(expensePositionObj);
        ExpensePosition expensePositionToUpdate = expensePositionRepository
                .findById(expensePositionObj.getId())
                .orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("Provide incorrect position id: {0} for expense: {1}, position with this id not exists.",
                                expensePositionObj.getId(), expense.getExpenseName())));

        expensePositionToUpdate.setExpensePositionName(expensePositionObj.getPositionName());
        expensePositionToUpdate.setCost(BigDecimal.valueOf(expensePositionObj.getCost()));
        expensePositionToUpdate.setSize(BigDecimal.valueOf(expensePositionObj.getSize()));
        return expensePositionRepository.save(expensePositionToUpdate);
    }
}
