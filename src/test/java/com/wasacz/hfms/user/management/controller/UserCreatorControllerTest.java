package com.wasacz.hfms.user.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserCreatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenCreateUserWithRoleAdmin_givenCreateUserRequest_thenReturnStatusCreatedWithBody() throws Exception {
        this.mockMvc.perform(post("/api/user")
                    .content(asJsonString(getCreateUserRequest()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    @ParameterizedTest
    @WithMockUser(authorities = "ROLE_ADMIN")
    @NullAndEmptySource
    public void whenCreateUserWithRoleAdmin_givenCreateUserRequestWithBlankFirstName_thenReturnBadRequestStatusWithErrors(String username) throws Exception {
        CreateUserRequest createUserRequest = getCreateUserRequest();
        createUserRequest.setUsername(username);
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username cannot be blank."));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenCreateUserWithRoleAdmin_givenCreateUserRequestWithIncorrectRole_thenReturnBadRequestStatusWithErrorMsg() throws Exception {
        CreateUserRequest createUserRequest = getCreateUserRequest();
        createUserRequest.setRole("ROLE_THAT_NOT_EXISTS");
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString(createUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Provided incorrect role."));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenCreateUserWithRoleUser_givenCreateUserRequest_thenReturnStatusForbiden() throws Exception {
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString( getCreateUserRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void whenCreateUserAsAnonymousUser_givenCreateUserRequest_thenReturnStatusUnauthorized() throws Exception {
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString( getCreateUserRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private CreateUserRequest getCreateUserRequest() {
        return CreateUserRequest.builder().username("Username").password("Password1@").role("ROLE_USER").build();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}