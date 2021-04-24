package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.finance.AbstractFinance;
import com.wasacz.hfms.finance.AbstractFinanceResponse;
import com.wasacz.hfms.finance.FinanceType;
import com.wasacz.hfms.finance.IFinanceService;
import com.wasacz.hfms.finance.expense.Controller.ExpenseResponse;
import com.wasacz.hfms.finance.shop.ShopManagementService;
import com.wasacz.hfms.finance.shop.ShopValidator;
import com.wasacz.hfms.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService implements IFinanceService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ShopRepository shopRepository;
    private final ExpensePositionService expensePositionService;
    private final ReceiptFileService receiptFileService;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseCategoryRepository expenseCategoryRepository, ShopManagementService shopManagementService, ShopRepository shopRepository, ExpensePositionService expensePositionService, ReceiptFileService receiptFileService) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.shopRepository = shopRepository;
        this.expensePositionService = expensePositionService;
        this.receiptFileService = receiptFileService;
    }

    private ExpenseCategory obtainCategory(Long categoryId, User user) {
        if(categoryId == null) {
            throw new IllegalStateException("CategoryId cannot be null!");
        }
        return expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Category with id " + categoryId + " not found."));
    }

    private Shop obtainShop(ExpenseObj expenseObj, User user) {
        if(expenseObj.getShop() != null && expenseObj.getShop().getId() != null) {
            return shopRepository.findByIdAndUserAndIsDeletedFalse(expenseObj.getShop().getId(), user).orElseThrow(() -> new IllegalArgumentException("Shop with id " + expenseObj.getShop().getId() + " not found."));
        }

        if(expenseObj.getShop() != null && expenseObj.getShop().getShopName() != null) {
            ShopValidator.validate(expenseObj.getShop());
            return shopRepository.save(Shop.builder().shopName(expenseObj.getShop().getShopName()).build());
        }
        return null;

    }

    @Override
    public ExpenseResponse add(AbstractFinance expenseObj, User user, MultipartFile file) {
        if(!(expenseObj instanceof ExpenseObj)) {
            throw new IllegalStateException("Incorrect object!");
        }
        ExpenseObj expense = (ExpenseObj) expenseObj;
        ExpenseValidator.validateFinance(expense);
        Expense savedExpense = expenseRepository.save(Expense.builder()
                .expenseName(expense.getExpenseName())
                .category(obtainCategory(expense.getCategoryId(), user))
                .cost(BigDecimal.valueOf(expense.getCost()))
                .shop(obtainShop(expense, user))
                .user(user)
                .build());
        ReceiptFile receiptFile = receiptFileService.saveFile(file, savedExpense, expense.getExpenseName(), user.getUsername());
        List<ExpensePosition> expensePositionList = expensePositionService.addExpensePositions(savedExpense, expense.getExpensePositions());
        return ExpenseMapper.mapExpenseToResponse(savedExpense, expensePositionList, receiptFile != null ? receiptFile.getId() : null);
    }

    @Override
    public List<AbstractFinanceResponse> getAll(User user) {
        Optional<List<Expense>> expensesByUser = expenseRepository.findAllByUser(user);
        List<Expense> allExpenses = expensesByUser.orElseGet(Collections::emptyList);
        return allExpenses.stream().map(expense -> {
            Optional<List<ExpensePosition>> expensePositionList = expensePositionService.getExpensePositionList(expense.getId());
            Optional<ReceiptFile> receiptFile = receiptFileService.getFileByExpense(expense.getId());
            return ExpenseMapper.mapExpenseToResponse(expense,
                    expensePositionList.orElse(Collections.emptyList()),
                    receiptFile.map(ReceiptFile::getId).orElse(null));
        }).collect(Collectors.toList());
    }

    @Override
    public FinanceType getService() {
        return FinanceType.EXPENSE;
    }
}
