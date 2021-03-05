package com.wasacz.hfms.user.authorization.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasacz.hfms.security.JwtTokenProvider;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "admin", authorities = "ROLE_ADMIN")
    public void whenAuthenticateUser_givenCreatedUser_thenReturnOkStatus() throws Exception {
        //given
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString(CreateUserRequest.builder().username("test").password("Test123!@#").role("ROLE_USER").build()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        //when and then
        this.mockMvc.perform(post("/api/auth/sign")
                .content(asJsonString(getAuthRequest("test", "Test123!@#")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenAuthenticateAnnonymousUser_thenReturnUnathorizedStatus() throws Exception {

        this.mockMvc.perform(post("/api/auth/sign")
                .content(asJsonString(getAuthRequest("test", "Test123!@#")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private AuthRequest getAuthRequest(String username, String password) {
        return new AuthRequest(username, password);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}