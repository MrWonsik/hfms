package com.wasacz.hfms.helpers;

import com.wasacz.hfms.finance.category.expense.ExpenseCategoryObj;
import com.wasacz.hfms.finance.shop.ShopObj;
import com.wasacz.hfms.security.UserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ShopCreatorStatic {

    public static MvcResult callCreateShopEndpoint(MockMvc mockMvc, String shopName, UserPrincipal user) throws Exception {
        return mockMvc.perform(post("/api/shop/").with(user(user))
                .content(asJsonString(getCreateShopRequest(shopName)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public static ShopObj getCreateShopRequest(String categoryName) {
        return ShopObj.builder().shopName(categoryName).build();
    }
}
