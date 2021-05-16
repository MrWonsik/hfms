package com.wasacz.hfms.finance.transaction.income;

import com.wasacz.hfms.finance.transaction.*;
import com.wasacz.hfms.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IncomeService implements ITransactionService {

    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    public IncomeService(IncomeRepository incomeRepository, IncomeCategoryRepository incomeCategoryRepository) {
        this.incomeRepository = incomeRepository;
        this.incomeCategoryRepository = incomeCategoryRepository;
    }

    @Override
    public AbstractTransactionResponse add(AbstractTransaction incomeObj, User user, MultipartFile file) {
        IncomeObj income = obtainIncomeObj(incomeObj);
        TransactionValidator.validateFinance(income);
        Income savedIncome = incomeRepository.save(buildIncome(incomeObj, user, income));

        return IncomeMapper.mapIncomeToResponse(savedIncome);
    }

    private IncomeObj obtainIncomeObj(AbstractTransaction incomeObj) {
        if (!(incomeObj instanceof IncomeObj)) {
            throw new IllegalStateException("Incorrect abstractFinance implementation!");
        }
        return (IncomeObj) incomeObj;
    }

    private Income buildIncome(AbstractTransaction incomeObj, User user, IncomeObj income) {
        Income.IncomeBuilder incomeBuilder = Income.builder()
                .incomeName(income.getName())
                .category(obtainCategory(income.getCategoryId(), user))
                .amount(BigDecimal.valueOf(income.getAmount()))
                .user(user);
        if (incomeObj.getTransactionDate() != null) {
            incomeBuilder.incomeDate(incomeObj.getTransactionDate());
        }
        return incomeBuilder.build();
    }

    private IncomeCategory obtainCategory(Long categoryId, User user) {
        if (categoryId == null) {
            throw new IllegalStateException("CategoryId cannot be null!");
        }
        return incomeCategoryRepository.findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Category with id " + categoryId + " not found."));
    }

    @Override
    public List<AbstractTransactionResponse> getAll(User user) {
        Optional<List<Income>> incomesByUser = incomeRepository.findAllByUser(user);
        List<Income> allIncomes = incomesByUser.orElseGet(Collections::emptyList);
        return allIncomes.stream().map(IncomeMapper::mapIncomeToResponse).collect(Collectors.toList());
    }

    @Override
    public List<AbstractTransactionResponse> getAllForMonthInYear(User user, YearMonth yearMonth) {
        Optional<List<Income>> incomesByUserFromMonth = incomeRepository.findAllByUserAndIncomeDateIsBetween(user, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        List<Income> allIncomes = incomesByUserFromMonth.orElseGet(Collections::emptyList);
        return allIncomes.stream().map(IncomeMapper::mapIncomeToResponse).collect(Collectors.toList());
    }

    @Override
    public AbstractTransactionResponse delete(long incomeId, User user) {
        Income incomeToDelete = incomeRepository.findByIdAndUser(incomeId, user).orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(incomeId)));
        incomeRepository.delete(incomeToDelete);
        return IncomeMapper.mapIncomeToResponse(incomeToDelete);
    }

    @Override
    public TransactionType getService() {
        return TransactionType.INCOME;
    }

    @Override
    public AbstractTransactionResponse updateTransaction(Long incomeId, AbstractTransaction incomeObj, User user) {
        IncomeObj income = obtainIncomeObj(incomeObj);
        Income incomeToUpdate = incomeRepository.findByIdAndUser(incomeId, user).orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(incomeId)));

        TransactionValidator.validateFinance(income);
        incomeToUpdate.setIncomeName(income.getName());
        incomeToUpdate.setIncomeDate(income.getTransactionDate());
        incomeToUpdate.setAmount(BigDecimal.valueOf(income.getAmount()));
        incomeToUpdate.setCategory(obtainCategory(income.getCategoryId(), user));
        Income savedUpdatedIncome = incomeRepository.save(incomeToUpdate);

        return IncomeMapper.mapIncomeToResponse(savedUpdatedIncome);
    }

    @Override
    public BigDecimal getSummaryAmountOfCategoryForMonth(long categoryId, YearMonth yearMonth) {
        List<Income> incomes = incomeRepository.findAllByIncomeDateIsBetweenAndCategoryId(yearMonth.atDay(1), yearMonth.atEndOfMonth(), categoryId).orElse(Collections.emptyList());
        return incomes.stream().map(Income::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public AbstractTransactionResponse getTheOldestTransactionForCategory(long categoryId) {
        Optional<Income> oldestIncome = incomeRepository.findFirstByCategoryIdOrderByIncomeDateAsc(categoryId);
        return oldestIncome.map(IncomeMapper::mapIncomeToResponse).orElse(null);
    }
}
