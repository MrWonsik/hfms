package com.wasacz.hfms.finance.transaction.expense.expensePositions;

import com.wasacz.hfms.finance.transaction.TransactionValidator;
import com.wasacz.hfms.persistence.Expense;
import com.wasacz.hfms.persistence.ExpensePosition;
import com.wasacz.hfms.persistence.ExpensePositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExpensePositionService {

    private final ExpensePositionRepository expensePositionRepository;

    public ExpensePositionService(ExpensePositionRepository expensePositionRepository) {
        this.expensePositionRepository = expensePositionRepository;
    }

    public List<ExpensePosition> addExpensePositions(Expense expense, List<ExpensePositionObj> expensePositions) {
        if(expense == null) {
            log.debug("Expense cannot be null");
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
        TransactionValidator.validateExpensePosition(expensePositionObj);
        ExpensePosition saved = expensePositionRepository.save(ExpensePosition.builder()
                .expense(expense)
                .expensePositionName(expensePositionObj.getPositionName())
                .amount(BigDecimal.valueOf(expensePositionObj.getAmount()))
                .size(BigDecimal.valueOf(expensePositionObj.getSize()))
                .build());
        log.debug("Expense position has been saved: " + saved.getExpensePositionName() + " for expense: " + saved.getExpense().getExpenseName());
        return saved;
    }

    public List<ExpensePosition> updateExpensePositions(Expense updatedExpense, List<ExpensePositionObj> newExpensePositions) {
        if(newExpensePositions == null) {
            return Collections.emptyList();
        }
        if(updatedExpense == null) {
            log.debug("Expense cannot be null");
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
        TransactionValidator.validateExpensePosition(expensePositionObj);
        ExpensePosition expensePositionToUpdate = expensePositionRepository
                .findById(expensePositionObj.getId())
                .orElseThrow(() -> {
                    String msg = MessageFormat.format("Provide incorrect position id: {0} for expense: {1}, position with this id not exists.",
                            expensePositionObj.getId(), expense.getExpenseName());
                    log.warn(msg);
                    throw new IllegalStateException(msg);
                });

        expensePositionToUpdate.setExpensePositionName(expensePositionObj.getPositionName());
        expensePositionToUpdate.setAmount(BigDecimal.valueOf(expensePositionObj.getAmount()));
        expensePositionToUpdate.setSize(BigDecimal.valueOf(expensePositionObj.getSize()));
        ExpensePosition saved = expensePositionRepository.save(expensePositionToUpdate);
        log.debug("Expense position has been updated: " + saved.getExpensePositionName() + " for expense: " + saved.getExpense().getExpenseName());
        return saved;
    }
}
