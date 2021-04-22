package com.wasacz.hfms.finance.expense;

import com.wasacz.hfms.finance.shop.ShopManagementService;
import com.wasacz.hfms.finance.shop.ShopValidator;
import com.wasacz.hfms.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseService {

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

    @Transactional
    public ExpenseResponse addExpense(ExpenseObj expenseObj, User user, MultipartFile receiptFile) {
        ExpenseValidator.validateExpense(expenseObj);
        Expense savedExpense = expenseRepository.save(Expense.builder()
                .expenseName(expenseObj.getExpenseName())
                .category(obtainCategory(expenseObj.getCategoryId(), user))
                .cost(BigDecimal.valueOf(expenseObj.getCost()))
                .shop(obtainShop(expenseObj, user))
                .build());
        receiptFileService.saveFile(receiptFile, savedExpense.getId(), expenseObj.getExpenseName(), user.getUsername());
        List<ExpensePosition> expensePositionList = expensePositionService.addExpensePositions(savedExpense, expenseObj.getExpensePositions());
        return ExpenseMapper.mapExpenseToResponse(savedExpense, expensePositionList, null);
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
}
