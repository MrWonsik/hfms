package com.wasacz.hfms.utils;

import com.wasacz.hfms.finance.category.CategoryServiceFactory;
import com.wasacz.hfms.finance.category.CategoryType;
import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.transaction.TransactionServiceFactory;
import com.wasacz.hfms.finance.transaction.TransactionType;
import com.wasacz.hfms.finance.transaction.expense.ExpenseObj;
import com.wasacz.hfms.finance.transaction.income.IncomeObj;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.service.UserManagementService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExampleUserFactory {

    private final UserManagementService userManagementService;
    private final TransactionServiceFactory transactionServiceFactory;
    private final UserRepository userRepository;
    private final CategoryServiceFactory categoryServiceFactory;

    public ExampleUserFactory(UserManagementService userManagementService, TransactionServiceFactory transactionServiceFactory, UserRepository userRepository, CategoryServiceFactory categoryServiceFactory) {
        this.userManagementService = userManagementService;
        this.transactionServiceFactory = transactionServiceFactory;
        this.userRepository = userRepository;
        this.categoryServiceFactory = categoryServiceFactory;
    }


    public void produceBasicUsers() {
        userManagementService.createUser(CreateUserRequest.builder().username("admin").password("Admin123!@").role("ROLE_ADMIN").build());
        userManagementService.createUser(CreateUserRequest.builder().username("user").password("User123!@").role("ROLE_USER").build());
    }

    public void produceExampleUser() {
        Optional<User> example = userRepository.findByUsername("example");
        if (example.isPresent()) {
            deleteAndCreateExampleUser(example.get());
        } else {
            createExampleUser();
        }
    }

    private void deleteAndCreateExampleUser(User user) {
        userRepository.delete(user);
        createExampleUser();
    }

    private void createExampleUser() {
        userManagementService.createUser(CreateUserRequest.builder().username("example").password("Example123!@").role("ROLE_USER").build());
        User user = userRepository.findByUsername("example").orElseThrow(() -> new IllegalStateException("Something goes wrong while creating example user..."));
        createExampleTransactions(user);
    }

    private void createExampleTransactions(User user) {
        createExampleExpenseTransactions(user, 200);
        createExampleIncomeTransactions(user, 200);
    }

    private void createExampleIncomeTransactions(User user, int quantity) {
        CategoriesResponse allIncomeCategories = categoryServiceFactory.getService(CategoryType.INCOME).getAllCategories(user);
        for (int i=0; i<quantity;i++) {
            IncomeObj transaction = createRandomIncome(allIncomeCategories);
            transactionServiceFactory.getService(TransactionType.INCOME).add(transaction, user, null);
        }
    }

    private IncomeObj createRandomIncome(CategoriesResponse allCategories) {
        Random random = new Random();
        return IncomeObj.builder()
                .name("Random income")
                .amount(2000 * random.nextDouble())
                .categoryId(allCategories.getCategories().get(random.nextInt(allCategories.getCategories().size())).getId())
                .transactionType("INCOME")
                .transactionDate(getRandomDate())
                .build();
    }

    private void createExampleExpenseTransactions(User user, int quantity) {
        CategoriesResponse allExpenseCategories = categoryServiceFactory.getService(CategoryType.EXPENSE).getAllCategories(user);
        for (int i=0; i<quantity;i++) {
            ExpenseObj transaction = createRandomExpense(allExpenseCategories);
            transactionServiceFactory.getService(TransactionType.EXPENSE).add(transaction, user, null);
        }
    }

    private ExpenseObj createRandomExpense(CategoriesResponse allCategories) {
        Random random = new Random();
        return ExpenseObj.builder()
                .expenseName("Random expense")
                .amount(2000 * random.nextDouble())
                .categoryId(allCategories.getCategories().get(random.nextInt(allCategories.getCategories().size())).getId())
                .transactionType("EXPENSE")
                .transactionDate(getRandomDate())
                .build();
    }

    private LocalDate getRandomDate() {
        Instant now = Instant.now();
        Instant threeYearAgo = Instant.now().minus(Duration.ofDays(3*365));
        return LocalDate.ofInstant(Instant.ofEpochSecond(ThreadLocalRandom.current().nextLong(threeYearAgo.getEpochSecond(), now.getEpochSecond())), ZoneId.systemDefault());
    }
}
