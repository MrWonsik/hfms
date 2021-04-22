package com.wasacz.hfms.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.expense.ExpenseObj;
import com.wasacz.hfms.finance.expense.ExpensePositionObj;
import com.wasacz.hfms.finance.expense.ExpenseResponse;
import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.finance.shop.ShopResponse;
import com.wasacz.hfms.helpers.CategoryCreatorStatic;
import com.wasacz.hfms.helpers.CurrentUserMock;
import com.wasacz.hfms.helpers.FileToMultipartFileConverter;
import com.wasacz.hfms.helpers.ShopCreatorStatic;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.security.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExpenseControllerIntegrationTest {

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

    @BeforeAll
    public void setup() throws Exception {
        //TODO: delete all images from savedReceipt before start tests here...
        currentUser = currentUserMock.getCurrentUser("User_expense", Role.ROLE_USER);
        MvcResult shop = ShopCreatorStatic.callCreateShopEndpoint(mockMvc, "existing_shop", currentUser);
        shopResponse = objectMapper.readValue(shop.getResponse().getContentAsString(), ShopResponse.class);
        MvcResult category = CategoryCreatorStatic.callCreateExpenseCategoryEndpoint(mockMvc, "categoryName", currentUser);
        expenseCategoryResponse = objectMapper.readValue(category.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithShopName_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .shop(ShopObj.builder().shopName("new_ikea").build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        MvcResult expense = this.mockMvc.perform(multipart("/api/expense/").file(FileToMultipartFileConverter.convertFileToMultiPart("src/test/resources/receipt_test.jpg")).with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);

        assertEquals("expense_2021_04_22", expenseResponse.getExpenseName());
        assertEquals("new_ikea", expenseResponse.getShopName());
        assertEquals(129.99, expenseResponse.getCost());
        assertTrue(expenseResponse.getExpensePositionList().isEmpty());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithShopId_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        MvcResult expense = this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);


        assertEquals("expense_2021_04_22", expenseResponse.getExpenseName());
        assertEquals("existing_shop", expenseResponse.getShopName());
        assertEquals(129.99, expenseResponse.getCost());
        assertTrue(expenseResponse.getExpensePositionList().isEmpty());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithoutShopObj_thenReturnOkStatus() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .build();
        MvcResult expense = this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);


        assertEquals("expense_2021_04_22", expenseResponse.getExpenseName());
        assertNull(expenseResponse.getShopName());
        assertEquals(129.99, expenseResponse.getCost());
        assertTrue(expenseResponse.getExpensePositionList().isEmpty());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositions_thenReturnOkStatus() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj("position_el1", 1d, 12.01));
        expensePositions.add(createExpensePositionObj("position_el2", 0.98, 5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions)
                .build();
        MvcResult expense = this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseResponse expenseResponse = objectMapper.readValue(expense.getResponse().getContentAsString(), ExpenseResponse.class);


        assertEquals("expense_2021_04_22", expenseResponse.getExpenseName());
        assertNull(expenseResponse.getShopName());
        assertEquals(129.99, expenseResponse.getCost());
        assertEquals(2, expenseResponse.getExpensePositionList().size());
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositionsWithNNullPositionName_thenReturnOkStatus() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj(null, 1d, 12.01));
        expensePositions.add(createExpensePositionObj("position_el2", 0.98, 5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("positionName cannot be blank."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositionsWithIncorrectSize_thenReturnOkStatus() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj("position_el2", -0.98, 5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Size must be bigger than 0."));

        List<ExpensePositionObj> expensePositions2 = new ArrayList<>();
        expensePositions2.add(createExpensePositionObj("position_el2", null, 5.89));
        ExpenseObj expenseObj2 = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions2)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Size must be bigger than 0."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithExpensePositionsWithIncorrectCost_thenReturnBadRequest() throws Exception {
        List<ExpensePositionObj> expensePositions = new ArrayList<>();
        expensePositions.add(createExpensePositionObj("position_el2", 0.98, -5.89));
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Cost must be bigger than 0."));

        List<ExpensePositionObj> expensePositions2 = new ArrayList<>();
        expensePositions2.add(createExpensePositionObj("position_el2", 1d, null));
        ExpenseObj expenseObj2 = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .expensePositions(expensePositions2)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Cost must be bigger than 0."));
    }

    private ExpensePositionObj createExpensePositionObj(String name, Double size, Double cost) {
        return ExpensePositionObj.builder()
                .positionName(name)
                .size(size)
                .cost(cost)
                .build();
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithShopIdThatNotExists_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .shop(ShopObj.builder().id(99999999L).build())
                .cost(129.99)
                .categoryId(expenseCategoryResponse.getId())
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Shop with id 99999999 not found."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithCategoryIdThatNotExists_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .categoryId(99999L)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Category with id 99999 not found."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithoutCategoryId_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(129.99)
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("CategoryId cannot be null!"));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithoutShopName_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .cost(129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("expenseName cannot be blank."));
    }

    @Test
    public void whenAddExpense_givenExpenseObjRequestWithIncorrectCost_thenReturnBadRequest() throws Exception {
        ExpenseObj expenseObj = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(-129.99)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Cost must be bigger than 0."));

        ExpenseObj expenseObj2 = ExpenseObj.builder()
                .expenseName("expense_2021_04_22")
                .cost(null)
                .shop(ShopObj.builder().id(shopResponse.getId()).build())
                .categoryId(expenseCategoryResponse.getId())
                .build();
        this.mockMvc.perform(post("/api/expense/").with(user(currentUser))
                .content(asJsonString(expenseObj2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Cost must be bigger than 0."));
    }

}
