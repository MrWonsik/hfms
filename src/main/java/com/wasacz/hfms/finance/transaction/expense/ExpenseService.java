package com.wasacz.hfms.finance.transaction.expense;

import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.finance.transaction.*;
import com.wasacz.hfms.finance.shop.ShopManagementService;
import com.wasacz.hfms.finance.shop.ShopValidator;
import com.wasacz.hfms.finance.transaction.expense.expensePositions.ExpensePositionService;
import com.wasacz.hfms.finance.transaction.expense.receiptFile.FileReceiptResponse;
import com.wasacz.hfms.finance.transaction.expense.receiptFile.ReceiptFileService;
import com.wasacz.hfms.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService implements ITransactionService {

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

    @Override
    @Transactional
    public ExpenseResponse add(AbstractTransaction expenseObj, User user, MultipartFile file) {
        ExpenseObj expense = obtainExpenseObj(expenseObj);
        TransactionValidator.validateFinance(expense);
        Expense savedExpense = expenseRepository.save(buildExpense(expenseObj, user, expense));

        ReceiptFile receiptFile = receiptFileService.saveFile(file, savedExpense, user.getUsername());
        List<ExpensePosition> expensePositionList = expensePositionService.addExpensePositions(savedExpense, expense.getExpensePositions());
        return ExpenseMapper.mapExpenseToResponse(savedExpense, expensePositionList, receiptFile != null ? receiptFile.getId() : null);
    }

    private Expense buildExpense(AbstractTransaction expenseObj, User user, ExpenseObj expense) {
        Expense.ExpenseBuilder expenseBuilder = Expense.builder()
                .expenseName(expense.getName())
                .category(obtainCategory(expense.getCategoryId(), user))
                .amount(BigDecimal.valueOf(expense.getAmount()))
                .shop(obtainShop(expense.getShop(), user))
                .user(user);
        if(expenseObj.getTransactionDate() != null) {
            expenseBuilder.expenseDate(expenseObj.getTransactionDate());
        }
        return expenseBuilder.build();
    }

    private ExpenseCategory obtainCategory(Long categoryId, User user) {
        if(categoryId == null) {
            throw new IllegalStateException("CategoryId cannot be null!");
        }
        return expenseCategoryRepository.findByIdAndUserAndIsDeletedFalse(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Category with id " + categoryId + " not found."));
    }

    private Shop obtainShop(ShopObj shopObj, User user) {
        if(shopObj != null && shopObj.getId() != null) {
            return shopRepository.findByIdAndUserAndIsDeletedFalse(shopObj.getId(), user).orElseThrow(() -> new IllegalArgumentException("Shop with id " + shopObj.getId() + " not found."));
        }

        if(shopObj != null && shopObj.getName() != null) {
            ShopValidator.validate(shopObj);
            return shopRepository.save(Shop.builder().name(shopObj.getName()).user(user).build());
        }
        return null;

    }

    @Override
    public List<AbstractTransactionResponse> getAll(User user) {
        Optional<List<Expense>> expensesByUser = expenseRepository.findAllByUser(user);
        List<Expense> allExpenses = expensesByUser.orElseGet(Collections::emptyList);
        return allExpenses.stream().map(this::getExpenseResponse).collect(Collectors.toList());
    }

    @Override
    public List<AbstractTransactionResponse> getAllForMonthInYear(User user, YearMonth yearMonth) {
        Optional<List<Expense>> expensesByUserFromMonth = expenseRepository.findAllByUserAndExpenseDateIsBetween(user, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        List<Expense> allExpenses = expensesByUserFromMonth.orElseGet(Collections::emptyList);
        return allExpenses.stream().map(this::getExpenseResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AbstractTransactionResponse delete(long expenseId, User user) {
        Expense expenseToDelete = expenseRepository.findByIdAndUser(expenseId, user).orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(expenseId)));
        receiptFileService.deleteFileByExpense(expenseToDelete.getId());
        expenseRepository.delete(expenseToDelete);
        return ExpenseMapper.mapExpenseToResponse(expenseToDelete);
    }

    private ExpenseResponse getExpenseResponse(Expense expense) {
        Optional<List<ExpensePosition>> expensePositionList = expensePositionService.getExpensePositionList(expense.getId());
        Optional<ReceiptFile> receiptFile = receiptFileService.getReceiptFileByExpense(expense.getId());
        return ExpenseMapper.mapExpenseToResponse(expense,
                expensePositionList.orElse(Collections.emptyList()),
                receiptFile.map(ReceiptFile::getId).orElse(null));
    }

    @Override
    public TransactionType getService() {
        return TransactionType.EXPENSE;
    }

    @Override
    @Transactional
    public AbstractTransactionResponse updateTransaction(Long expenseId, AbstractTransaction expenseObj, User user) {
        ExpenseObj expense = obtainExpenseObj(expenseObj);
        Expense expenseToUpdate = expenseRepository.findByIdAndUser(expenseId, user).orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(expenseId)));

        TransactionValidator.validateFinance(expense);
        expenseToUpdate.setExpenseName(expense.getName());
        expenseToUpdate.setExpenseDate(expense.getTransactionDate());
        expenseToUpdate.setAmount(BigDecimal.valueOf(expense.getAmount()));
        expenseToUpdate.setCategory(obtainCategory(expense.getCategoryId(), user));
        expenseToUpdate.setShop(obtainShop(expense.getShop(), user));
        Expense savedUpdatedExpense = expenseRepository.save(expenseToUpdate);

        List<ExpensePosition> expensePositionList = expensePositionService.updateExpensePositions(savedUpdatedExpense, expense.getExpensePositions());
        return ExpenseMapper.mapExpenseToResponse(savedUpdatedExpense, expensePositionList, getReceiptId(savedUpdatedExpense));
    }

    private Long getReceiptId(Expense savedUpdatedExpense) {
        Optional<ReceiptFile> receiptFileByExpense = receiptFileService.getReceiptFileByExpense(savedUpdatedExpense.getId());
        return receiptFileByExpense.map(ReceiptFile::getId).orElse(null);
    }

    private ExpenseObj obtainExpenseObj(AbstractTransaction expenseObj) {
        if (!(expenseObj instanceof ExpenseObj)) {
            throw new IllegalStateException("Incorrect abstractFinance implementation!");
        }
        return (ExpenseObj) expenseObj;
    }

    public FileReceiptResponse getReceiptFileByExpense(Long expenseId, User user) {
        expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(expenseId)));

        ReceiptFile receiptFile = receiptFileService.getFile(expenseId);

        return receiptFileService.mapFileReceiptToResponse(receiptFile);
    }

    public void deleteReceiptFile(Long expenseId, User user) {
        expenseRepository.findByIdAndUser(expenseId, user).orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(expenseId)));
        receiptFileService.deleteFileByExpense(expenseId);
    }

    public FileReceiptResponse uploadReceiptFile(Long expenseId, MultipartFile file, User user) {
        Expense expenseToUploadFile = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new IllegalArgumentException("Transaction %s not found.".formatted(expenseId)));

        ReceiptFile receiptFile = receiptFileService.saveFile(file, expenseToUploadFile, user.getUsername());
        return receiptFileService.mapFileReceiptToResponse(receiptFile);
    }
}
