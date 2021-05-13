package com.wasacz.hfms.helpers;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.category.income.IncomeCategoryObj;
import com.wasacz.hfms.security.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class CategoryCreatorStatic {

    public static MvcResult callCreateExpenseCategoryEndpoint(MockMvc mockMvc, String categoryName, UserPrincipal user) throws Exception {
        return mockMvc.perform(post("/api/category/expense/").with(user(user))
                .content(asJsonString(getCreateExpenseCategoryRequest(categoryName)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public static MvcResult callCreateIncomeCategoryEndpoint(MockMvc mockMvc, String categoryName, UserPrincipal user) throws Exception {
        return mockMvc.perform(post("/api/category/income/").with(user(user))
                .content(asJsonString(getCreateIncomeCategoryRequest(categoryName)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
    }


    public static ExpenseCategoryObj getCreateExpenseCategoryRequest(String categoryName) {
        return ExpenseCategoryObj.builder().categoryName(categoryName).build();
    }

    public static IncomeCategoryObj getCreateIncomeCategoryRequest(String categoryName) {
        return IncomeCategoryObj.builder().categoryName(categoryName).build();
    }
}
