package com.wasacz.hfms.finance.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.finance.category.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.controller.dto.ExpenseCategoryMaximumAmountRequest;
import com.wasacz.hfms.finance.category.controller.dto.ExpenseCategoryVersionResponse;
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

import java.math.BigDecimal;
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
        currentUser = currentUserMock.createMockUser("User_category_versions", Role.ROLE_USER);
    }

    @Test
    void whenEditMaximumAmountExpenseCategory_givenExpenseCategoryMaximumAmountRequest_thenReturnOkStatus() throws Exception {
        ExpenseCategoryResponse categoryAndReturn = createCategoryAndReturn();

        ExpenseCategoryMaximumAmountRequest expenseCategoryMaximumAmountRequest = ExpenseCategoryMaximumAmountRequest
                .builder()
                .newMaximumAmount(100d)
                .isValidFromNextMonth(false)
                .build();

        YearMonth yearMonth = YearMonth.now();

        ExpenseCategoryVersionResponse expenseCategoryVersionResponse = callEditMaximumAmountExpenseCategory(categoryAndReturn, expenseCategoryMaximumAmountRequest);
        assertEquals(100d, expenseCategoryVersionResponse.getMaximumAmount());
        assertEquals(yearMonth, expenseCategoryVersionResponse.getValidMonth());
        assertEquals(categoryAndReturn.getCurrentVersion().getId(), expenseCategoryVersionResponse.getId());
    }

    private ExpenseCategoryVersionResponse callEditMaximumAmountExpenseCategory(ExpenseCategoryResponse categoryAndReturn, ExpenseCategoryMaximumAmountRequest expenseCategoryMaximumAmountRequest) throws Exception {
        MvcResult versionResponse = this.mockMvc.perform(put("/api/category/expense/" + categoryAndReturn.getId() + "/version").with(user(currentUser))
                .content(asJsonString(expenseCategoryMaximumAmountRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(versionResponse.getResponse().getContentAsString(), ExpenseCategoryVersionResponse.class);
    }

    private ExpenseCategoryResponse createCategoryAndReturn() throws Exception {
        ExpenseCategoryObj categoryObj = ExpenseCategoryObj
                .builder()
                .categoryName("Car")
                .maximumAmount(BigDecimal.valueOf(10d))
                .build();

        MvcResult createdCategoryResult = this.mockMvc.perform(post("/api/category/expense").with(user(currentUser))
                .content(asJsonString(categoryObj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(createdCategoryResult.getResponse().getContentAsString(), ExpenseCategoryResponse.class);
    }

    @Test
    void whenEditMaximumAmountExpenseCategory_givenExpenseCategoryMaximumAmountRequestForNextMonth_thenReturnOkStatus() throws Exception {
        ExpenseCategoryResponse categoryAndReturn = createCategoryAndReturn();

        ExpenseCategoryMaximumAmountRequest expenseCategoryMaximumAmountRequest = ExpenseCategoryMaximumAmountRequest
                .builder()
                .newMaximumAmount(100d)
                .isValidFromNextMonth(true)
                .build();

        YearMonth yearMonth = YearMonth.now();

        ExpenseCategoryVersionResponse expenseCategoryVersionResponse = callEditMaximumAmountExpenseCategory(categoryAndReturn, expenseCategoryMaximumAmountRequest);
        assertEquals(100d, expenseCategoryVersionResponse.getMaximumAmount());
        assertEquals(yearMonth.plusMonths(1), expenseCategoryVersionResponse.getValidMonth());
        assertNotEquals(categoryAndReturn.getCurrentVersion().getId(), expenseCategoryVersionResponse.getId());
    }

    @Test
    void whenEditMaximumAmountExpenseCategory_givenExpenseCategoryMaximumAmountRequestWithMinusNewMaximumAmount_thenReturnOkStatus() throws Exception {
        ExpenseCategoryResponse categoryAndReturn = createCategoryAndReturn();

        ExpenseCategoryMaximumAmountRequest expenseCategoryMaximumAmountRequest = ExpenseCategoryMaximumAmountRequest
                .builder()
                .newMaximumAmount(-100d)
                .isValidFromNextMonth(true)
                .build();

        this.mockMvc.perform(put("/api/category/expense/" + categoryAndReturn.getId() + "/version").with(user(currentUser))
                .content(asJsonString(expenseCategoryMaximumAmountRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Maximum amount should be grater than 0."));
    }
}