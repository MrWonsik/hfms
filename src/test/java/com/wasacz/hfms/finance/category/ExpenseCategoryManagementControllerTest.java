package com.wasacz.hfms.finance.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoriesResponse;
import com.wasacz.hfms.finance.category.expense.ExpenseCategoryResponse;
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
class ExpenseCategoryManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrentUserMock currentUserMock;

    private UserPrincipal currentUser;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        currentUser = currentUserMock.getCurrentUser("User_expense", Role.ROLE_USER);
    }

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryRequest_thenReturnOkStatus() throws Exception {
        //given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
                .builder()
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(false)
                .categoryType(CategoryType.EXPENSE)
                .build();

        MvcResult createdCategoryResult = this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ExpenseCategoryResponse expenseCategoryResponse = objectMapper.readValue(createdCategoryResult.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        assertFalse(expenseCategoryResponse.getExpenseCategoryVersions().isEmpty());
        assertNotNull(expenseCategoryResponse.getCurrentVersion());
    }

    @Test
    public void whenEditExpenseCategory_givenEditExpenseCategoryRequest_thenReturnOkStatus() throws Exception {
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
                .builder()
                .categoryName("Bike")
                .colorHex("#F00")
                .isFavourite(false)
                .categoryType(CategoryType.EXPENSE)
                .build();

        MvcResult createdCategory = this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        ExpenseCategoryResponse expenseCategoryResponse = objectMapper.readValue(createdCategory.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = CategoryIsFavouriteRequest.builder()
                .isFavourite(true)
                .categoryType(CategoryType.EXPENSE)
                .build();

        this.mockMvc.perform(patch("/api/category/" + expenseCategoryResponse.getId()).with(user(currentUser))
                .content(asJsonString(categoryIsFavouriteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favourite").value(true));
    }

    @Test
    public void whenEditExpenseCategory_givenIdThatNotExists_thenReturnBadRequest() throws Exception {

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = CategoryIsFavouriteRequest.builder()
                .isFavourite(true)
                .categoryType(CategoryType.EXPENSE)
                .build();

        this.mockMvc.perform(patch("/api/category/" + 101010L).with(user(currentUser))
                .content(asJsonString(categoryIsFavouriteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Expense category not found."));
    }

    @Test
    public void whenDeleteExpenseCategory_givenExpenseCategoryId_thenReturnOkStatus() throws Exception {
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
                .builder()
                .categoryName("Home")
                .colorHex("#F00")
                .isFavourite(false)
                .categoryType(CategoryType.EXPENSE)
                .build();

        MvcResult createdCategory = this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        ExpenseCategoryResponse expenseCategoryResponse = objectMapper.readValue(createdCategory.getResponse().getContentAsString(), ExpenseCategoryResponse.class);

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = CategoryIsFavouriteRequest.builder()
                .isFavourite(true)
                .categoryType(CategoryType.EXPENSE)
                .build();

        this.mockMvc.perform(delete("/api/expense-category/" + expenseCategoryResponse.getId()).with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted").value(true));
    }

    @Test
    public void whenDeleteExpenseCategory_givenIdThatNotExists_thenReturnBadRequest() throws Exception {

        CategoryIsFavouriteRequest categoryIsFavouriteRequest = CategoryIsFavouriteRequest.builder()
                .isFavourite(true)
                .categoryType(CategoryType.EXPENSE)
                .build();

        this.mockMvc.perform(delete("/api/expense-category/" + 101010L).with(user(currentUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Expense category not found."));
    }

    @Test
    public void whenGetAllExpenseCategoriesForNewUSer_thenReturnOkStatusAndEmptyListResponse() throws Exception {
        //given
        MvcResult expenseCategories = this.mockMvc.perform(get("/api/expense-category/").with(user(currentUserMock.getCurrentUser("New_user_expense", Role.ROLE_USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        ExpenseCategoriesResponse expenseCategoriesResponse = objectMapper.readValue(expenseCategories.getResponse().getContentAsString(), ExpenseCategoriesResponse.class);
        List<ExpenseCategoryResponse> expenseCategoriesListResponse = expenseCategoriesResponse.getExpenseCategories();
        assertTrue(expenseCategoriesListResponse.isEmpty());
    }

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryRequestOnlyWithName_thenReturnOkStatusAndHexColorIsRandom() throws Exception {
        //given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
                .builder()
                .categoryName("Car")
                .categoryType(CategoryType.EXPENSE)
                .build();

        MvcResult mvcResult = this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        ExpenseCategoryResponse expenseCategoryResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
        assertFalse(expenseCategoryResponse.getColorHex().isEmpty());
        assertEquals("Car", expenseCategoryResponse.getCategoryName());
        assertFalse(expenseCategoryResponse.isDeleted());
        assertFalse(expenseCategoryResponse.isFavourite());

    }

    @Test
    public void whenAddExpenseCategory_givenCreateExpenseCategoryRequestWithIncorrectHexColor_thenReturnBadRequest() throws Exception {
        //given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest
                .builder()
                .categoryName("Car")
                .colorHex("F00000")
                .isFavourite(false)
                .categoryType(CategoryType.EXPENSE)
                .build();

        this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Incorrect hex color provided."));
    }

    @Test
    public void whenAddExpenseCategoryWithoutCategoryName_thenReturnBadRequestStatus() throws Exception {
        //given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder().categoryType(CategoryType.EXPENSE)
                .build();

        this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("categoryName cannot be blank."));
    }

    @Test
    public void whenAddCategoryWithEmptyRequestBody_thenReturnBadRequestStatus() throws Exception {
        //given
        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder().build();

        this.mockMvc.perform(post("/api/category/").with(user(currentUser))
                .content(asJsonString(createCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Category is required."));
    }


}