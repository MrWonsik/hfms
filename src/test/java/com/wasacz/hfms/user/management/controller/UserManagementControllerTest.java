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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenCreateUserWithRoleAdmin_givenCreateUserRequest_thenReturnStatusCreatedWithBody() throws Exception {
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString(getCreateUserRequest("Username")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isEnabled").value(true));
    }

    @ParameterizedTest
    @WithMockUser(authorities = "ROLE_ADMIN")
    @NullAndEmptySource
    public void whenCreateUserWithRoleAdmin_givenCreateUserRequestWithBlankFirstName_thenReturnBadRequestStatusWithErrors(String username) throws Exception {
        CreateUserRequest createUserRequest = getCreateUserRequest("Any");
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
        CreateUserRequest createUserRequest = getCreateUserRequest("Any");
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
                .content(asJsonString(getCreateUserRequest("Any")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void whenCreateUserAsAnonymousUser_givenCreateUserRequest_thenReturnStatusUnauthorized() throws Exception {
        this.mockMvc.perform(post("/api/user")
                .content(asJsonString(getCreateUserRequest("Any")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenEditUserAsRoleAdmin_givenEditUserRequest_thenReturnStatusOk() throws Exception {
        MvcResult result = callCreateUserEndpoint("UserToEdit1");

        var userId = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class).getId();

        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).password("NewSuperSecurePassword1!@#").build();

        this.mockMvc.perform(put("/api/user/" + userId)
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("UserToEdit1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isEnabled").value(false));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenEditUserAsRoleUser_givenEditUserRequest_thenReturnStatusForbiden() throws Exception {
        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).password("NewSuperSecurePassword1!@#").build();

        this.mockMvc.perform(put("/api/user/1")
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void whenEditUserAsAnonymousUser_givenEditUserRequest_thenReturnStatusUnauthorised() throws Exception {
        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).password("NewSuperSecurePassword1!@#").build();

        this.mockMvc.perform(put("/api/user/1")
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenEditUserAsRoleAdmin_givenEditUserRequestEmpty_thenReturnStatusOk() throws Exception {
        MvcResult result = callCreateUserEndpoint("UserToEdit2");

        var userId = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class).getId();

        EditUserRequest editUserRequest = EditUserRequest.builder().build();

        this.mockMvc.perform(put("/api/user/" + userId)
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("UserToEdit2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isEnabled").value(true));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenEditUserAsRoleAdmin_givenEditUserRequestWithTooShortPassword_thenReturnStatusBadRequest() throws Exception {
        MvcResult result = callCreateUserEndpoint("UserToEdit3");

        var userId = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class).getId();

        EditUserRequest editUserRequest = EditUserRequest.builder().password("admin").build();

        this.mockMvc.perform(put("/api/user/" + userId)
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Password don't meet rules."));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenEditUserAsRoleAdmin_givenEditUserRequestAndIncorrectId_thenReturnStatusBadRequest() throws Exception {
        EditUserRequest editUserRequest = EditUserRequest.builder().build();

        this.mockMvc.perform(put("/api/user/999999")
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with id 999999 not found."));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenDeleteUserAsRoleAdmin_givenCreatedUserId_thenReturnStatusOk() throws Exception {
        MvcResult result = callCreateUserEndpoint("UserToDelete1");

        var userId = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class).getId();

        this.mockMvc.perform(delete("/api/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenDeleteUserAsRoleAdmin_givenIncorrectUserId_thenReturnStatusBadRequest() throws Exception {
        this.mockMvc.perform(delete("/api/user/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with id 999999 not found."));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenDeleteUserAsRoleUser_givenEditUserRequest_thenReturnStatusForbiden() throws Exception {
        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).password("NewSuperSecurePassword1!@#").build();

        this.mockMvc.perform(delete("/api/user/1")
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void whenDeleteUserAsAnonymousUser_givenEditUserRequest_thenReturnStatusUnauthorised() throws Exception {
        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).password("NewSuperSecurePassword1!@#").build();

        this.mockMvc.perform(delete("/api/user/1")
                .content(asJsonString(editUserRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private MvcResult callCreateUserEndpoint(String username) throws Exception {
        return this.mockMvc.perform(post("/api/user")
                .content(asJsonString(getCreateUserRequest(username)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private CreateUserRequest getCreateUserRequest(String username) {
        return CreateUserRequest.builder().username(username).password("Password1@").role("ROLE_USER").build();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}