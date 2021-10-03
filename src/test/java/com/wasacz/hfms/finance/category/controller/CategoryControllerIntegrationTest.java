package com.wasacz.hfms.finance.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.AbstractCategory;
import com.wasacz.hfms.finance.category.CategoryServiceType;
import com.wasacz.hfms.finance.category.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.IncomeCategoryObj;
import com.wasacz.hfms.finance.category.controller.dto.*;
import com.wasacz.hfms.helpers.CurrentUserMock;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.security.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrentUserMock currentUserMock;

    private UserPrincipal currentUser;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        currentUser = currentUserMock.createMockUser("User_category", Role.ROLE_USER);
    }

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryRequest_thenReturnOkStatus() throws Exception {
        //given
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj
                .builder()
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        var expenseCategoryResponse = (ExpenseCategoryResponse) createCategoryAndReturn(categoryObj, CategoryServiceType.EXPENSE);

        assertFalse(expenseCategoryResponse.getExpenseCategoryVersions().isEmpty());
        assertNotNull(expenseCategoryResponse.getCurrentVersion());
    }

    private AbstractCategoryResponse createCategoryAndReturn(AbstractCategory categoryObj, CategoryServiceType type) throws Exception {
        MvcResult createdCategoryResult = this.mockMvc.perform(post("/api/category/" + type.name() + "/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        if(type.equals(CategoryServiceType.EXPENSE)) {
            return objectMapper.readValue(createdCategoryResult.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
        } else {
            return objectMapper.readValue(createdCategoryResult.getResponse().getContentAsString(), IncomeCategoryResponse.class);
        }
    }

    @Test
    public void whenEditExpenseCategory_givenEditExpenseCategoryRequest_thenReturnOkStatus() throws Exception {
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj
                .builder()
                .categoryName("Bike")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        MvcResult createdCategory = this.mockMvc.perform(post("/api/category/expense/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        ExpenseCategoryResponse expenseCategoryResponse = objectMapper.readValue(createdCategory.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = new CategoryIsFavouriteRequest(true);

        this.mockMvc.perform(patch("/api/category/expense/favourite/" + expenseCategoryResponse.getId()).with(user(currentUser))
                .content(asJsonString(categoryIsFavouriteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favourite").value(true));
    }

    @Test
    public void whenEditExpenseCategory_givenIdThatNotExists_thenReturnBadRequest() throws Exception {

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = new CategoryIsFavouriteRequest(true);


        this.mockMvc.perform(patch("/api/category/expense/favourite/" + 101010L).with(user(currentUser))
                .content(asJsonString(categoryIsFavouriteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Expense category not found."));
    }

    @Test
    public void whenDeleteExpenseCategory_givenExpenseCategoryId_thenReturnOkStatus() throws Exception {
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj
                .builder()
                .categoryName("Home")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        MvcResult createdCategory = this.mockMvc.perform(post("/api/category/expense/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        ExpenseCategoryResponse expenseCategoryResponse = objectMapper.readValue(createdCategory.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        this.mockMvc.perform(delete("/api/category/expense/" + expenseCategoryResponse.getId()).with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));
    }

    @Test
    public void whenDeleteExpenseCategory_givenIdThatNotExists_thenReturnBadRequest() throws Exception {

        this.mockMvc.perform(delete("/api/category/expense/" + 101010L).with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Expense category not found."));
    }

    @Test
    public void whenGetAllExpenseCategoriesForNewUSer_thenReturnOkStatusAndEmptyListResponse() throws Exception {
        //given
        MvcResult expenseCategories = this.mockMvc.perform(get("/api/category/expense/").with(user(currentUserMock.createMockUser("New_user_expense", Role.ROLE_USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        CategoriesResponse categoryResponse = objectMapper.readValue(expenseCategories.getResponse().getContentAsString(), CategoriesResponse.class);
        List<ExpenseCategoryResponse> expenseCategoriesListResponse = (List<ExpenseCategoryResponse>) categoryResponse.getCategories();
        assertTrue(expenseCategoriesListResponse.isEmpty());
    }

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryRequestOnlyWithName_thenReturnOkStatusAndHexColorIsRandom() throws Exception {
        //given
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj
                .builder()
                .categoryName("Car")
                .build();

        var expenseCategoryResponse = createCategoryAndReturn(categoryObj, CategoryServiceType.EXPENSE);

        assertFalse(expenseCategoryResponse.getColorHex().isEmpty());
        assertEquals("Car", expenseCategoryResponse.getCategoryName());
        assertFalse(expenseCategoryResponse.isDeleted());
        assertFalse(expenseCategoryResponse.isFavourite());

    }

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryRequestWithIncorrectHexColor_thenReturnBadRequest() throws Exception {
        //given
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj
                .builder()
                .categoryName("Car")
                .colorHex("F00000")
                .isFavourite(false)
                .build();

        this.mockMvc.perform(post("/api/category/expense/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Incorrect hex color provided."));
    }

    @Test
    public void whenAddExpenseCategoryWithoutCategoryName_thenReturnBadRequestStatus() throws Exception {
        //given
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj.builder().build();

        this.mockMvc.perform(post("/api/category/expense/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Field: categoryName cannot be blank."));
    }

    @Test
    public void whenAddCategoryWithWrongCategoryType_thenReturnBadRequestStatus() throws Exception {
        //given

        this.mockMvc.perform(post("/api/category/emptyCategory/").with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Incorrect category type."));
    }

    @Test
    public void whenAddIncomeCategory_givenCreateCategoryRequest_thenReturnOkStatus() throws Exception {
        //given
        IncomeCategoryObj categoryObj = IncomeCategoryObj
                .builder()
                .categoryName("Work")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        var incomeCategoryResponse = createCategoryAndReturn(categoryObj, CategoryServiceType.INCOME);
        assertFalse(incomeCategoryResponse.isFavourite());
        assertEquals("#F00", incomeCategoryResponse.getColorHex());
    }

    @Test
    public void whenSetAsFavouriteIncomeCategory_givenIsFavoriteCategoryRequest_thenReturnOkStatus() throws Exception {
        IncomeCategoryObj categoryObj = IncomeCategoryObj
                .builder()
                .categoryName("Bike")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        var incomeCategoryResponse = createCategoryAndReturn(categoryObj, CategoryServiceType.INCOME);

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = new CategoryIsFavouriteRequest(true);

        this.mockMvc.perform(patch("/api/category/income/favourite/" + incomeCategoryResponse.getId()).with(user(currentUser))
                .content(asJsonString(categoryIsFavouriteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favourite").value(true));
    }

    @Test
    public void whenSetAsFavouriteIncomeCategory_givenIdThatNotExists_thenReturnBadRequest() throws Exception {

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = new CategoryIsFavouriteRequest(true);

        this.mockMvc.perform(patch("/api/category/income/favourite/" + 101010L).with(user(currentUser))
                .content(asJsonString(categoryIsFavouriteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Income category not found."));
    }

    @Test
    public void whenDeleteIncomeCategory_givenIncomeCategoryId_thenReturnOkStatus() throws Exception {
        IncomeCategoryObj categoryObj = IncomeCategoryObj
                .builder()
                .categoryName("Home")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        var incomeCategoryResponse = createCategoryAndReturn(categoryObj, CategoryServiceType.INCOME);

        this.mockMvc.perform(delete("/api/category/income/" + incomeCategoryResponse.getId()).with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));
    }

    @Test
    public void whenDeleteIncomeCategory_givenIdThatNotExists_thenReturnBadRequest() throws Exception {

        this.mockMvc.perform(delete("/api/category/income/" + 101010L).with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Income category not found."));
    }

    @Test
    public void whenGetAllIncomeCategoriesForNewUSer_thenReturnOkStatusAndEmptyListResponse() throws Exception {
        //given
        MvcResult expenseCategories = this.mockMvc.perform(get("/api/category/income/").with(user(currentUserMock.createMockUser("New_user_income", Role.ROLE_USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        CategoriesResponse categoryResponse = objectMapper.readValue(expenseCategories.getResponse().getContentAsString(), CategoriesResponse.class);
        List<IncomeCategoryResponse> expenseCategoriesListResponse = (List<IncomeCategoryResponse>) categoryResponse.getCategories();
        assertTrue(expenseCategoriesListResponse.isEmpty());
    }

    @Test
    public void whenAddIncomeCategory_givenCreateCategoryRequestOnlyWithName_thenReturnOkStatusAndHexColorIsRandom() throws Exception {
        //given
        IncomeCategoryObj categoryObj = IncomeCategoryObj
                .builder()
                .categoryName("Work")
                .build();

        var incomeCategoryResponse = createCategoryAndReturn(categoryObj, CategoryServiceType.INCOME);

        assertFalse(incomeCategoryResponse.getColorHex().isEmpty());
        assertEquals("Work", incomeCategoryResponse.getCategoryName());
        assertFalse(incomeCategoryResponse.isDeleted());
        assertFalse(incomeCategoryResponse.isFavourite());

    }

    @Test
    public void whenAddIncomeCategory_givenCreateCategoryRequestWithIncorrectHexColor_thenReturnBadRequest() throws Exception {
        //given
        IncomeCategoryObj categoryObj = IncomeCategoryObj
                .builder()
                .categoryName("Work")
                .colorHex("F00000")
                .isFavourite(false)
                .build();

        this.mockMvc.perform(post("/api/category/income/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Incorrect hex color provided."));
    }

    @Test
    public void whenAddIncomeCategoryWithoutCategoryName_thenReturnBadRequestStatus() throws Exception {
        //given
        IncomeCategoryObj categoryObj = IncomeCategoryObj.builder().build();

        this.mockMvc.perform(post("/api/category/income/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Field: categoryName cannot be blank."));
    }

    @Test
    public void whenEditIncomeCategory_thenReturnOkStatus() throws Exception {
        //given
        IncomeCategoryObj categoryObj = IncomeCategoryObj
                .builder()
                .categoryName("Bike")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        MvcResult createdCategory = this.mockMvc.perform(post("/api/category/income/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        IncomeCategoryResponse incomeCategoryResponse = objectMapper.readValue(createdCategory.getResponse().getContentAsString(), IncomeCategoryResponse.class);

        EditCategoryRequest editCategoryRequest = EditCategoryRequest.builder().categoryName("NewName").colorHex("#aaa").build();

        MvcResult editedCategory = this.mockMvc.perform(patch("/api/category/income/" + incomeCategoryResponse.getId()).with(user(currentUser))
                .content(asJsonString(editCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        IncomeCategoryResponse editedCategoryResponse = objectMapper.readValue(editedCategory.getResponse().getContentAsString(), IncomeCategoryResponse.class);

        assertEquals("NewName", editedCategoryResponse.getCategoryName());
        assertEquals("#aaa", editedCategoryResponse.getColorHex());

    }
}