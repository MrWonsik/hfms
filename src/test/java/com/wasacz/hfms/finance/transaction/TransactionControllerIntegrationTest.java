package com.wasacz.hfms.finance.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.controller.dto.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.category.controller.dto.IncomeCategoryResponse;
import com.wasacz.hfms.finance.transaction.expense.ExpenseResponse;
import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.finance.shop.ShopResponse;
import com.wasacz.hfms.finance.transaction.expense.ExpenseObj;
import com.wasacz.hfms.finance.transaction.expense.expensePositions.ExpensePositionObj;
import com.wasacz.hfms.finance.transaction.income.IncomeObj;
import com.wasacz.hfms.finance.transaction.income.IncomeResponse;
import com.wasacz.hfms.helpers.CategoryCreatorStatic;
import com.wasacz.hfms.helpers.CurrentUserMock;
import com.wasacz.hfms.helpers.FileToMultipartFileConverter;
import com.wasacz.hfms.helpers.ShopCreatorStatic;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.security.UserPrincipal;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionControllerIntegrationTest {

    @Value("${app.receipt.storage.path}")
    private String destinationPath;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrentUserMock currentUserMock;

    private UserPrincipal currentUser;

    private ShopResponse shopResponse;

    private ExpenseCategoryResponse expenseCategoryResponse;
    
    private IncomeCategoryResponse incomeCategoryResponse;

    @BeforeAll
    public void setup() throws Exception {
        File directory = new File(destinationPath);
        if(directory.exists()) {
            FileUtils.cleanDirectory(directory);
        }
        currentUser = currentUserMock.createMockUser("User_expense", Role.ROLE_USER);
        MvcResult shop = ShopCreatorStatic.callCreateShopEndpoint(mockMvc, "existing_shop", currentUser);
        shopResponse = objectMapper.readValue(shop.getResponse().getContentAsString(), ShopResponse.class);
        MvcResult expenseCategory = CategoryCreatorStatic.callCreateExpenseCategoryEndpoint(mockMvc, "categoryName_expense", currentUser);
        expenseCategoryResponse = objectMapper.readValue(expenseCategory.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
        MvcResult incomeCategory = CategoryCreatorStatic.callCreateIncomeCategoryEndpoint(mockMvc, "categoryName_income", currentUser);
        incomeCategoryResponse = objectMapper.readValue(incomeCategory.getResponse().getContentAsString(), IncomeCategoryResponse.class);
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithShopName_thenReturnOkStatus() throws Exception {
        MvcResult expense = createExpense("expense_2021_04_22", 129.99, "new_ikea", currentUser, expenseCategoryResponse.getId(), LocalDate.now());

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        assertEquals("expense_2021_04_22", expenseResponse.getName());
        assertEquals("new_ikea", expenseResponse.getShop().getName());
        assertEquals(129.99, expenseResponse.getAmount());
        assertTrue(expenseResponse.getExpensePositionList().isEmpty());
        assertNull(expenseResponse.getReceiptId());
    }

    @Test
    public void whenAddIncome_givenIncomeObjRequestWithShopName_thenReturnOkStatus() throws Exception {
        MvcResult income = createIncome("income_2021_04_22", 129.99, currentUser, incomeCategoryResponse.getId(), LocalDate.now());

        IncomeResponse incomeResponse = objectMapper.readValue(income.getResponse().getContentAsString(), IncomeResponse.class);

        assertEquals("income_2021_04_22", incomeResponse.getName());
        assertEquals(129.99, incomeResponse.getAmount());
    }

    private MvcResult createExpense(String expenseName, Double amount, String shopName, UserPrincipal user, Long categoryId, LocalDate date) throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName(expenseName)
                .amount(amount)
                .shop(ShopObj.builder().name(shopName).build())
                .categoryId(categoryId)
                .transactionDate(date)
                .build();
        return this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult createIncome(String incomeName, Double amount, UserPrincipal user, Long categoryId, LocalDate date) throws Exception {
        IncomeObj incomeObj = IncomeObj.builder()
                .name(incomeName)
                .amount(amount)
                .categoryId(categoryId)
                .transactionDate(date)
                .build();
        return this.mockMvc.perform(multipart("/api/transaction/income/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(incomeObj).getBytes()))
                .with(user(user))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void whenGetAllExpense_givenExpenseObjRequestWithShopName_thenReturnOkStatus() throws Exception {
        UserPrincipal user = currentUserMock.createMockUser("User_expense_for_get_all", Role.ROLE_USER);
        MvcResult category = CategoryCreatorStatic.callCreateExpenseCategoryEndpoint(mockMvc, "Shoping", user);
        ExpenseCategoryResponse categoryResponse = objectMapper.readValue(category.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        createExpense("Icecream",12.99,"Biedronka", user, categoryResponse.getId(), LocalDate.now());
        createExpense("Milk",3.00,"Biedronka", user, categoryResponse.getId(), LocalDate.now());
        createExpense("Egg",8.99,"Biedronka", user, categoryResponse.getId(), LocalDate.now());

        MvcResult expenseList = this.mockMvc.perform(get("/api/transaction/expense").with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ExpenseResponse> expenseResponse = Arrays.asList(objectMapper.readValue(expenseList.getResponse().getContentAsString(), ExpenseResponse[].class));

        assertEquals(3, expenseResponse.size());
    }

    @Test
    public void whenGetAllIncome_givenIncomeObjRequest_thenReturnOkStatus() throws Exception {
        UserPrincipal user = currentUserMock.createMockUser("User_income_for_get_all", Role.ROLE_USER);
        MvcResult category = CategoryCreatorStatic.callCreateIncomeCategoryEndpoint(mockMvc, "Salary", user);
        IncomeCategoryResponse categoryResponse = objectMapper.readValue(category.getResponse().getContentAsString(), IncomeCategoryResponse.class);

        createIncome("Icecream",12.99, user, categoryResponse.getId(), LocalDate.now());
        createIncome("Milk",3.00, user, categoryResponse.getId(), LocalDate.now());
        createIncome("Egg",8.99, user, categoryResponse.getId(), LocalDate.now());

        MvcResult incomeList = this.mockMvc.perform(get("/api/transaction/income").with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<IncomeResponse> incomeResponse = Arrays.asList(objectMapper.readValue(incomeList.getResponse().getContentAsString(), IncomeResponse[].class));

        assertEquals(3, incomeResponse.size());
    }

    @Test
    public void whenGetAllExpenseFromCurrentMonth_givenExpenseObjRequestWithShopName_thenReturnOkStatusAndReturnExpenseOnlyFromCurrentMonth() throws Exception {
        UserPrincipal user = currentUserMock.createMockUser("User_expense_for_get_all_month", Role.ROLE_USER);
        MvcResult category = CategoryCreatorStatic.callCreateExpenseCategoryEndpoint(mockMvc, "Shoping", user);
        ExpenseCategoryResponse categoryResponse = objectMapper.readValue(category.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        createExpense("Icecream",12.99,"Biedronka", user, categoryResponse.getId(), LocalDate.now().minusMonths(1));
        createExpense("Milk",3.00,"Biedronka", user, categoryResponse.getId(), LocalDate.now().minusMonths(1));
        createExpense("Egg",8.99,"Biedronka", user, categoryResponse.getId(), LocalDate.now());

        int year = Year.now().getValue();
        int month = LocalDate.now().getMonth().getValue();


        MvcResult expenseList = this.mockMvc.perform(get("/api/transaction/expense?year=" + year + "&month=" + month).with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ExpenseResponse> expenseResponse = Arrays.asList(objectMapper.readValue(expenseList.getResponse().getContentAsString(), ExpenseResponse[].class));

        assertEquals(1, expenseResponse.size());
    }

    @Test
    public void whenGetAllIncomeFromCurrentMonth_givenIncomeObjRequest_thenReturnOkStatusAndReturnIncomeOnlyFromCurrentMonth() throws Exception {
        UserPrincipal user = currentUserMock.createMockUser("User_income_for_get_all_month", Role.ROLE_USER);
        MvcResult category = CategoryCreatorStatic.callCreateIncomeCategoryEndpoint(mockMvc, "Shoping", user);
        IncomeCategoryResponse categoryResponse = objectMapper.readValue(category.getResponse().getContentAsString(), IncomeCategoryResponse.class);

        createIncome("Icecream",12.99, user, categoryResponse.getId(), LocalDate.now().minusMonths(1));
        createIncome("Milk",3.00, user, categoryResponse.getId(), LocalDate.now().minusMonths(1));
        createIncome("Egg",8.99, user, categoryResponse.getId(), LocalDate.now());

        int year = Year.now().getValue();
        int month = LocalDate.now().getMonth().getValue();


        MvcResult incomeList = this.mockMvc.perform(get("/api/transaction/income?year=" + year + "&month=" + month).with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<IncomeResponse> incomeResponse = Arrays.asList(objectMapper.readValue(incomeList.getResponse().getContentAsString(), IncomeResponse[].class));

        assertEquals(1, incomeResponse.size());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithShopIdAndReceiptFile_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(FileToMultipartFileConverter.convertFileToMultiPart("src/test/resources/receipt_test.jpg"))
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);


        assertEquals("expense_2021_04_22", expenseResponse.getName());
        assertEquals(shopResponse.getName(), expenseResponse.getShop().getName());
        assertEquals(shopResponse.getId(), expenseResponse.getShop().getId());
        assertEquals(129.99, expenseResponse.getAmount());
        assertTrue(expenseResponse.getExpensePositionList().isEmpty());
        assertNotNull(expenseResponse.getReceiptId());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithoutShopObj_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);


        assertEquals("expense_2021_04_22", expenseResponse.getName());
        assertNull(expenseResponse.getShop());
        assertEquals(129.99, expenseResponse.getAmount());
        assertTrue(expenseResponse.getExpensePositionList().isEmpty());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositions_thenReturnOkStatus() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj("position_el1", 1d, 12.01));
        expensePositions.add(createExpensePositionObj("position_el2", 0.98, 5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions)
                .transactionDate(LocalDate.now())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);


        assertEquals("expense_2021_04_22", expenseResponse.getName());
        assertNull(expenseResponse.getShop());
        assertEquals(129.99, expenseResponse.getAmount());
        assertEquals(2, expenseResponse.getExpensePositionList().size());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositionsWithNullPositionName_thenReturnBadRequest() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj(null, 1d, 12.01));
        expensePositions.add(createExpensePositionObj("position_el2", 0.98, 5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions)
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Field: positionName cannot be blank."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositionsWithIncorrectSize_thenReturnBadRequest() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj("position_el2", -0.98, 5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .expensePositions(expensePositions)
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Size must be bigger than 0."));

        List<ExpensePositionObj> expensePositions2 = new ArrayList<>();
        expensePositions2.add(createExpensePositionObj("position_el2", null, 5.89));
        ExpenseObj expenseObj2 = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .transactionDate(LocalDate.now())
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions2)
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj2).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Size must be bigger than 0."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositionsWithIncorrectAmount_thenReturnBadRequest() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj("position_el2", 0.98, -5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .expensePositions(expensePositions)
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Amount must be bigger than 0."));

        List<ExpensePositionObj> expensePositions2 = new ArrayList<>();
        expensePositions2.add(createExpensePositionObj("position_el2", 1d, null));
        ExpenseObj expenseObj2 = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions2)
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .content(asJsonString(expenseObj2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Amount must be bigger than 0."));
    }

    private ExpensePositionObj createExpensePositionObj(String name, Double size, Double amount) {
        return ExpensePositionObj.builder()
                .positionName(name)
                .size(size)
                .amount(amount)
                .build();
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithShopIdThatNotExists_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .shop(ShopObj.builder().id(99999999L).build())
                .amount(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Shop with id 99999999 not found."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithCategoryIdThatNotExists_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .categoryId(99999L)
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Category with id 99999 not found."));
    }

    @Test
    public void whenAddIncome_givenIncomeObjRequestWithCategoryIdThatNotExists_thenReturnBadRequest() throws Exception {
        IncomeObj incomeObj = IncomeObj.builder()
                .name("income_2021_04_22")
                .amount(129.99)
                .categoryId(99999L)
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/income/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(incomeObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Category with id 99999 not found."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithoutCategoryId_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(129.99)
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("CategoryId cannot be null!"));
    }

    @Test
    public void whenAddIncome_givenIncomeObjRequestWithoutCategoryId_thenReturnBadRequest() throws Exception {
        IncomeObj incomeObj = IncomeObj.builder()
                .name("income_2021_04_22")
                .amount(129.99)
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/income/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(incomeObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("CategoryId cannot be null!"));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithoutShopName_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .amount(129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Field: name cannot be blank."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithIncorrectAmount_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(-129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Amount must be bigger than 0."));

        ExpenseObj expenseObj2 = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .amount(null)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/expense/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(expenseObj).getBytes()))
                .with(user(currentUser))
                .content(asJsonString(expenseObj2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Amount must be bigger than 0."));
    }

    @Test
    public void whenAddIncome_givenIncomeObjRequestWithIncorrectAmount_thenReturnBadRequest() throws Exception {
        IncomeObj incomeObj = IncomeObj.builder()
                .name("income_2021_04_22")
                .amount(-129.99)
                .categoryId(incomeCategoryResponse.getId())
                .transactionDate(LocalDate.now())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/income/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(incomeObj).getBytes()))
                .with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Amount must be bigger than 0."));

        IncomeObj incomeObj2 = IncomeObj.builder()
                .name("income_2021_04_22")
                .amount(null)
                .categoryId(incomeCategoryResponse.getId())
                .build();
        this.mockMvc.perform(multipart("/api/transaction/income/")
                .file(new MockMultipartFile("transaction", "", "application/json", objectMapper.writeValueAsString(incomeObj).getBytes()))
                .with(user(currentUser))
                .content(asJsonString(incomeObj2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Amount must be bigger than 0."));
    }
    
    @Test
    public void whenDeleteExpense_givenExpenseId_thenReturnOkStatus() throws Exception {
        MvcResult expense = createExpense("expense_2021_05_09", 129.99, "new_ikea", currentUser, expenseCategoryResponse.getId(), LocalDate.now());

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        this.mockMvc.perform(delete("/api/transaction/expense/" + expenseResponse.getId()).with(user(currentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteExpense_givenIncorrectExpenseId_thenReturnBadRequest() throws Exception {
        this.mockMvc.perform(delete("/api/transaction/expense/" + 9999999L).with(user(currentUser)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Transaction 9999999 not found."));
    }

    @Test
    public void whenDeleteIncome_givenIncomeId_thenReturnOkStatus() throws Exception {
        MvcResult income = createIncome("income_2021_05_09", 129.99, currentUser, incomeCategoryResponse.getId(), LocalDate.now());

        IncomeResponse incomeResponse = objectMapper.readValue(income.getResponse().getContentAsString(), IncomeResponse.class);

        this.mockMvc.perform(delete("/api/transaction/income/" + incomeResponse.getId()).with(user(currentUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteIncome_givenIncorrectIncomeId_thenReturnBadRequest() throws Exception {
        this.mockMvc.perform(delete("/api/transaction/income/" + 9999999L).with(user(currentUser)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Transaction 9999999 not found."));
    }

    @Test
    public void whenUpdateExpense_givenExpenseRequest_thenReturnOkStatus() throws Exception {
        MvcResult expense = createExpense("expense_2021_05_09", 129.99, "new_ikea", currentUser, expenseCategoryResponse.getId(), LocalDate.now());

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        ExpenseObj expenseObjectBody = ExpenseObj.builder()
                .expenseName("Updated_name")
                .amount(2000d)
                .transactionDate(LocalDate.now())
                .shop(ShopObj.builder().name("updated_shop").build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        MvcResult updatedExpenseResult = this.mockMvc.perform(put("/api/transaction/expense/" + expenseResponse.getId())
                .with(user(currentUser))
                .content(asJsonString(expenseObjectBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse updatedExpenseResponse = objectMapper.readValue(updatedExpenseResult.getResponse().getContentAsString(), ExpenseResponse.class);
        assertEquals("Updated_name", updatedExpenseResponse.getName());
        assertEquals(expenseObjectBody.getShop().getName(), updatedExpenseResponse.getShop().getName());
        assertEquals(2000d, updatedExpenseResponse.getAmount());
        assertTrue(updatedExpenseResponse.getExpensePositionList().isEmpty());
    }

    @Test
    public void whenUpdateIncome_givenIncomeRequest_thenReturnOkStatus() throws Exception {
        LocalDate now = LocalDate.now();
        MvcResult income = createIncome("income_2021_05_09", 129.99, currentUser, incomeCategoryResponse.getId(), now);

        IncomeResponse incomeResponse = objectMapper.readValue(income.getResponse().getContentAsString(), IncomeResponse.class);

        IncomeObj incomeObjectBody = IncomeObj.builder()
                .name("Updated_name")
                .amount(2000d)
                .transactionDate(now.minusDays(20))
                .categoryId(incomeCategoryResponse.getId())
                .build();
        MvcResult updatedIncomeResult = this.mockMvc.perform(put("/api/transaction/income/" + incomeResponse.getId())
                .with(user(currentUser))
                .content(asJsonString(incomeObjectBody))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        IncomeResponse updatedIncomeResponse = objectMapper.readValue(updatedIncomeResult.getResponse().getContentAsString(), IncomeResponse.class);
        assertEquals("Updated_name", updatedIncomeResponse.getName());
        assertEquals(2000d, updatedIncomeResponse.getAmount());
    }
}
