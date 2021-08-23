package com.wasacz.hfms.finance.shop;

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

import java.util.List;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShopManagementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CurrentUserMock currentUserMock;

    private UserPrincipal currentUser;

    @BeforeAll
    public void setup() {
        currentUser = currentUserMock.createMockUser("User", Role.ROLE_USER);
    }

    @Test
    public void whenAddShop_givenNewShopRequest_thenReturnOkStatus() throws Exception {
        //given
        ShopObj ikea = ShopObj.builder().name("ikea").build();
        this.mockMvc.perform(post("/api/shop").with(user(currentUser))
                .content(asJsonString(ikea))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void whenAddShopsWithEmptyRequestBody_thenReturnBadRequestStatus() throws Exception {
        //given
        ShopObj ikea = ShopObj.builder().build();

        this.mockMvc.perform(post("/api/shop").with(user(currentUser))
                .content(asJsonString(ikea))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Field: shopName cannot be blank."));
    }

    @Test
    public void whenAddShopsWithTheSameNameAsDifferentUser_thenReturnOkStatus() throws Exception {
        //given
        ShopObj ikea = ShopObj.builder().name("ikea3").build();

        this.mockMvc.perform(post("/api/shop").with(user(currentUser))
                .content(asJsonString(ikea))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        UserPrincipal user2 = currentUserMock.createMockUser("DifferentUser", Role.ROLE_USER);
        this.mockMvc.perform(post("/api/shop").with(user(user2))
                .content(asJsonString(ikea))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenDeleteShop_thenReturnOkStatus() throws Exception {
        //given
        ShopObj ikea = ShopObj.builder().name("ikea4").build();

        ShopResponse shopResponse = objectMapper.readValue(createNewShop(ikea).getResponse().getContentAsString(), ShopResponse.class);
        this.mockMvc.perform(delete("/api/shop/" + shopResponse.getId()).with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetAllShops_thenReturnOkStatusAndOnlyNotDeletedShop() throws Exception {
        //given
        ShopObj ikea = ShopObj.builder().name("ikea5").build();
        ShopObj ikea2 = ShopObj.builder().name("ikea5").build();


        MvcResult newShop = createNewShop(ikea);
        createNewShop(ikea2);
        ShopResponse shopResponse1 = objectMapper.readValue(newShop.getResponse().getContentAsString(), ShopResponse.class);
        this.mockMvc.perform(delete("/api/shop/" + shopResponse1.getId()).with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult shops = this.mockMvc.perform(get("/api/shop").with(user(currentUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        ShopsResponse shopsResponse = objectMapper.readValue(shops.getResponse().getContentAsString(), ShopsResponse.class);
        List<ShopResponse> shopsListResponse = shopsResponse.getShops();
        shopsListResponse.forEach(shop -> assertFalse(shop.isDeleted()));
    }

    @Test
    public void whenGetAllShopsForNewUSer_thenReturnOkStatusAndEmptyListResponse() throws Exception {
        //given
        MvcResult shops = this.mockMvc.perform(get("/api/shop").with(user(currentUserMock.createMockUser("New_user", Role.ROLE_USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        ShopsResponse shopsResponse = objectMapper.readValue(shops.getResponse().getContentAsString(), ShopsResponse.class);
        List<ShopResponse> shopsListResponse = shopsResponse.getShops();
        assertTrue(shopsListResponse.isEmpty());
    }

    private MvcResult createNewShop(ShopObj ikea) throws Exception {
        return this.mockMvc.perform(post("/api/shop").with(user(currentUser))
                .content(asJsonString(ikea))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();
    }

}