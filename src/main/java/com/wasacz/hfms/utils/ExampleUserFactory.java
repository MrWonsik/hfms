package com.wasacz.hfms.utils;

import com.wasacz.hfms.finance.ServiceType;
import com.wasacz.hfms.finance.category.ExpenseCategoryService;
import com.wasacz.hfms.finance.category.IncomeCategoryService;
import com.wasacz.hfms.finance.category.controller.AbstractCategoryResponse;
import com.wasacz.hfms.finance.category.controller.CategoriesResponse;
import com.wasacz.hfms.finance.category.ExpenseCategoryVersionService;
import com.wasacz.hfms.finance.transaction.TransactionServiceFactory;
import com.wasacz.hfms.finance.transaction.expense.ExpenseObj;
import com.wasacz.hfms.finance.transaction.income.IncomeObj;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.service.UserManagementService;
import com.wasacz.hfms.utils.randomizer.Randomizer;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExampleUserFactory {

    private final Random random = new Random();
    private final HashMap<String, Randomizer.Properties> incomeCategories;
    private final HashMap<String, Randomizer.Properties> expenseCategories;


    private final UserManagementService userManagementService;
    private final TransactionServiceFactory transactionServiceFactory;
    private final UserRepository userRepository;
    private final ExpenseCategoryService expenseCategoryService;
    private final IncomeCategoryService incomeCategoryService;
    private final ExpenseCategoryVersionService expenseCategoryVersionService;

    public ExampleUserFactory(UserManagementService userManagementService, TransactionServiceFactory transactionServiceFactory, UserRepository userRepository, ExpenseCategoryService expenseCategoryService, IncomeCategoryService incomeCategoryService, Randomizer randomizer, ExpenseCategoryVersionService expenseCategoryVersionService) {
        this.userManagementService = userManagementService;
        this.transactionServiceFactory = transactionServiceFactory;
        this.userRepository = userRepository;
        this.expenseCategoryService = expenseCategoryService;
        this.incomeCategoryService = incomeCategoryService;
        incomeCategories = randomizer.getIncomeCategories();
        expenseCategories = randomizer.getExpenseCategories();
        this.expenseCategoryVersionService = expenseCategoryVersionService;
    }


    public void generateAdminUser() {
        Optional<User> admin = userRepository.findByUsername("admin");
        if(admin.isEmpty()) {
            userManagementService.createUser(CreateUserRequest.builder().username("admin").password("Admin123!@").role("ROLE_ADMIN").build());
        }
    }

    public void genarateExampleUser() {
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
        createExamplePlannedAmounts(user);
        createExampleTransactions(user);
    }

    private void createExamplePlannedAmounts(User user) {
        CategoriesResponse allExpenseCategories = expenseCategoryService.getAllCategories(user);
        allExpenseCategories.getCategories().forEach(abstractCategoryResponse -> {
                    if (expenseCategories.containsKey(abstractCategoryResponse.getCategoryName())) {
                        Randomizer.Properties properties = expenseCategories.get(abstractCategoryResponse.getCategoryName());
                        expenseCategoryVersionService.updateCategoryVersion(user, abstractCategoryResponse.getId(), properties.getPlannedAmount().doubleValue(), YearMonth.now());
                    }
                });
    }

    private void createExampleTransactions(User user) {
        createExampleExpenseTransactions(user);
        createExampleIncomeTransactions(user);
    }

    private void createExampleIncomeTransactions(User user) {
        CategoriesResponse allIncomeCategories = incomeCategoryService.getAllCategories(user);
        createSalaryIncomes(user, allIncomeCategories);
        createRandomIncomes(user, allIncomeCategories, "Selling", 4);
        createRandomIncomes(user, allIncomeCategories, "Gifts", 4);
    }

    private void createSalaryIncomes(User user, CategoriesResponse allIncomeCategories) {
        AbstractCategoryResponse categoryAbstract = getCategoryByName(allIncomeCategories, "Salary");
        if (categoryAbstract == null) return;
        Randomizer.Properties categoryProperties = incomeCategories.get( categoryAbstract.getCategoryName());
        Randomizer.Product product = categoryProperties.getProducts().get(0); // It's salary index
        LocalDate date = LocalDate.now();
        for(int i=0; i<25; i++) {
            IncomeObj income = IncomeObj.builder()
                    .name(product.getName())
                    .amount(Math.random() * (product.getMaxAmount().doubleValue() - product.getMinAmount().doubleValue()) + product.getMinAmount().doubleValue())
                    .categoryId(categoryAbstract.getId())
                    .transactionDate(date)
                    .build();
            transactionServiceFactory.getService(ServiceType.INCOME).add(income, user, null);
            date = date.minusMonths(1L);
        }
    }

    private void createRandomIncomes(User user, CategoriesResponse allCategories, String categoryName, int quantity) {
        AbstractCategoryResponse category = getCategoryByName(allCategories, categoryName);
        if (category == null) return;
        Randomizer.Properties categoryProperties = incomeCategories.get( category.getCategoryName());
        Randomizer.Product product = categoryProperties.getProducts().get(random.nextInt(categoryProperties.getProducts().size()));
        for(int i=0; i<quantity; i++) {
            IncomeObj income = IncomeObj.builder()
                    .name(product.getName())
                    .amount(Math.random() * (product.getMaxAmount().doubleValue() - product.getMinAmount().doubleValue()) + product.getMinAmount().doubleValue())
                    .categoryId(category.getId())
                    .transactionDate(getRandomDate())
                    .build();
            transactionServiceFactory.getService(ServiceType.INCOME).add(income, user, null);
        }
    }

    private void createExampleExpenseTransactions(User user) {
        CategoriesResponse allExpenseCategories = expenseCategoryService.getAllCategories(user);
        createBillsExpenses(user, allExpenseCategories);
        createRandomExpense(user, allExpenseCategories, "Grocery Shopping", 200);
        createRandomExpense(user, allExpenseCategories, "Transport", 50);
        createRandomExpense(user, allExpenseCategories, "Car", 24);
        createRandomExpense(user, allExpenseCategories, "Culture and entertainment", 24);
        createRandomExpense(user, allExpenseCategories, "Health and beauty", 24);
        createRandomExpense(user, allExpenseCategories, "Personal development", 24);
        createRandomExpense(user, allExpenseCategories, "Home", 24);
        createRandomExpense(user, allExpenseCategories, "Clothes and accessories", 48);
        createRandomExpense(user, allExpenseCategories, "Other", 100);

    }

    private void createBillsExpenses(User user, CategoriesResponse allExpenseCategories) {
        AbstractCategoryResponse categoryAbstract = getCategoryByName(allExpenseCategories, "Bills");
        if (categoryAbstract == null) return;
        Randomizer.Properties categoryProperties = expenseCategories.get( categoryAbstract.getCategoryName());
        categoryProperties.getProducts().forEach(product -> {
            LocalDate date = LocalDate.now();
            for(int i=0; i<25; i++) {
                ExpenseObj income = ExpenseObj.builder()
                        .expenseName(product.getName())
                        .amount(Math.random() * (product.getMaxAmount().doubleValue() - product.getMinAmount().doubleValue()) + product.getMinAmount().doubleValue())
                        .categoryId(categoryAbstract.getId())
                        .transactionDate(date)
                        .build();
                transactionServiceFactory.getService(ServiceType.EXPENSE).add(income, user, null);
                date = date.minusMonths(1L);
            }
        });
    }

    private void createRandomExpense(User user, CategoriesResponse allCategories, String categoryName, int quantity) {
        AbstractCategoryResponse category = getCategoryByName(allCategories, categoryName);
        if (category == null) return;
        Randomizer.Properties categoryProperties = expenseCategories.get(categoryName);
        Randomizer.Product product = categoryProperties.getProducts().get(random.nextInt(categoryProperties.getProducts().size()));
        for(int i=0; i<quantity; i++) {
            ExpenseObj expense = ExpenseObj.builder()
                    .expenseName(product.getName())
                    .amount(Math.random() * (product.getMaxAmount().doubleValue() + product.getMinAmount().doubleValue()) + product.getMinAmount().doubleValue())
                    .categoryId(category.getId())
                    .transactionDate(getRandomDate())
                    .build();
            transactionServiceFactory.getService(ServiceType.EXPENSE).add(expense, user, null);
        }

    }

    private AbstractCategoryResponse getCategoryByName(CategoriesResponse allCategories, String categoryName) {
        Optional<? extends AbstractCategoryResponse> foundCategory = allCategories.getCategories().stream().filter(abstractCategoryResponse -> categoryName.contains(abstractCategoryResponse.getCategoryName())).findFirst();

        if (foundCategory.isEmpty()) {
            return null;
        }
        return foundCategory.get();
    }

    private LocalDate getRandomDate() {
        Instant now = Instant.now();
        Instant threeYearAgo = Instant.now().minus(Duration.ofDays(2*365));
        return LocalDate.ofInstant(Instant.ofEpochSecond(ThreadLocalRandom.current().nextLong(threeYearAgo.getEpochSecond(), now.getEpochSecond())), ZoneId.systemDefault());
    }
}
