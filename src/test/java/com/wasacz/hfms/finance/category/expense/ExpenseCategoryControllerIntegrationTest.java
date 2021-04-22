package com.wasacz.hfms.finance.category.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.controller.CategoryObj;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryMaximumCostRequest;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryResponse;
import com.wasacz.hfms.finance.category.expense.controller.ExpenseCategoryVersionResponse;
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

import java.time.YearMonth;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpenseCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurrentUserMock currentUserMock;

    private UserPrincipal currentUser;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        currentUser = currentUserMock.getCurrentUser("User_category_versions", Role.ROLE_USER);
    }

    @Test
    void whenEditMaximumCostExpenseCategory_givenExpenseCategoryMaximumCostRequest_thenReturnOkStatus() throws Exception {
        ExpenseCategoryResponse categoryAndReturn = createCategoryAndReturn();

        ExpenseCategoryMaximumCostRequest expenseCategoryMaximumCostRequest = ExpenseCategoryMaximumCostRequest
                .builder()
                .newMaximumCost(100d)
                .isValidFromNextMonth(false)
                .build();

        YearMonth yearMonth = YearMonth.now();

        ExpenseCategoryVersionResponse expenseCategoryVersionResponse = callEditMaximumCostExpenseCategory(categoryAndReturn, expenseCategoryMaximumCostRequest);
        assertEquals(100d, expenseCategoryVersionResponse.getMaximumCost());
        assertEquals(yearMonth, expenseCategoryVersionResponse.getValidMonth());
        assertEquals(categoryAndReturn.getCurrentVersion().getId(), expenseCategoryVersionResponse.getId());
    }

    private ExpenseCategoryVersionResponse callEditMaximumCostExpenseCategory(ExpenseCategoryResponse categoryAndReturn, ExpenseCategoryMaximumCostRequest expenseCategoryMaximumCostRequest) throws Exception {
        MvcResult versionResponse = this.mockMvc.perform(put("/api/category/expense/" + categoryAndReturn.getId() + "/version").with(user(currentUser))
                .content(asJsonString(expenseCategoryMaximumCostRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(versionResponse.getResponse().getContentAsString(), ExpenseCategoryVersionResponse.class);
    }

    private ExpenseCategoryResponse createCategoryAndReturn() throws Exception {
        CategoryObj categoryObj = CategoryObj
                .builder()
                .categoryName("Car")
                .maximumCost(10d)
                .build();

        MvcResult createdCategoryResult = this.mockMvc.perform(post("/api/category/expense/").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(createdCategoryResult.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
    }

    @Test
    void whenEditMaximumCostExpenseCategory_givenExpenseCategoryMaximumCostRequestForNextMonth_thenReturnOkStatus() throws Exception {
        ExpenseCategoryResponse categoryAndReturn = createCategoryAndReturn();

        ExpenseCategoryMaximumCostRequest expenseCategoryMaximumCostRequest = ExpenseCategoryMaximumCostRequest
                .builder()
                .newMaximumCost(100d)
                .isValidFromNextMonth(true)
                .build();

        YearMonth yearMonth = YearMonth.now();

        ExpenseCategoryVersionResponse expenseCategoryVersionResponse = callEditMaximumCostExpenseCategory(categoryAndReturn, expenseCategoryMaximumCostRequest);
        assertEquals(100d, expenseCategoryVersionResponse.getMaximumCost());
        assertEquals(yearMonth.plusMonths(1), expenseCategoryVersionResponse.getValidMonth());
        assertNotEquals(categoryAndReturn.getCurrentVersion().getId(), expenseCategoryVersionResponse.getId());
    }

    @Test
    void whenEditMaximumCostExpenseCategory_givenExpenseCategoryMaximumCostRequestWithMinusNewMaximumCost_thenReturnOkStatus() throws Exception {
        ExpenseCategoryResponse categoryAndReturn = createCategoryAndReturn();

        ExpenseCategoryMaximumCostRequest expenseCategoryMaximumCostRequest = ExpenseCategoryMaximumCostRequest
                .builder()
                .newMaximumCost(-100d)
                .isValidFromNextMonth(true)
                .build();

        this.mockMvc.perform(put("/api/category/expense/" + categoryAndReturn.getId() + "/version").with(user(currentUser))
                .content(asJsonString(expenseCategoryMaximumCostRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Maximum cost should be grater than 0."));
    }
}