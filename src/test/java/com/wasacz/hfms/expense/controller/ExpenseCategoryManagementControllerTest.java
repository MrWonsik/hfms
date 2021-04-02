package com.wasacz.hfms.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void whenAddExpenseCategory_givenCreateExepnseCategoryRequest_thenReturnOkStatus() throws Exception {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest
                .builder()
                .categoryName("Car")
                .colorHex("#F00")
                .isFavourite(false)
                .build();

        this.mockMvc.perform(post("/api/expense-category/").with(user(currentUser))
                .content(asJsonString(createExpenseCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenAddExpenseCategory_givenCreateExepnseCategoryRequestOnlyWithName_thenReturnOkStatusAndHexColorIsRandom() throws Exception {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest
                .builder()
                .categoryName("Car")
                .build();

        MvcResult mvcResult = this.mockMvc.perform(post("/api/expense-category/").with(user(currentUser))
                .content(asJsonString(createExpenseCategoryRequest))
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
    public void whenAddExpenseCategory_givenCreateExepnseCategoryRequestWithIncorrectHexColor_thenReturnBadRequest() throws Exception {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest
                .builder()
                .categoryName("Car")
                .colorHex("F00000")
                .isFavourite(false)
                .build();

        this.mockMvc.perform(post("/api/expense-category/").with(user(currentUser))
                .content(asJsonString(createExpenseCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Incorrect hex color provided."));
    }

    @Test
    public void whenAddExpenseCategoryWithEmptyRequestBody_thenReturnBadRequestStatus() throws Exception {
        //given
        CreateExpenseCategoryRequest createExpenseCategoryRequest = CreateExpenseCategoryRequest.builder().build();

        this.mockMvc.perform(post("/api/expense-category/").with(user(currentUser))
                .content(asJsonString(createExpenseCategoryRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("categoryName cannot be blank."));
    }


}