package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.helpers.UserCreatorStatic;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static com.wasacz.hfms.helpers.UserCreatorStatic.PASSWORD;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ChangePasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void whenChangePasswordAsAdmin_givenCorrectChangePasswordRequest_thenReturnStatusOk() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("newPassword!23@")
                .repeatedNewPassword("newPassword!23@")
                .oldPassword(PASSWORD)
                .build();

        User user = User.builder().username("Test").password(passwordEncoder.encode(PASSWORD)).role(Role.ROLE_ADMIN).id(1L).build();

        this.mockMvc.perform(post("/api/user/password").with(user(new UserPrincipal(user)))
                .content(asJsonString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void whenChangePasswordAsUser_givenCorrectChangePasswordRequest_thenReturnStatusOk() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("newPassword!23@")
                .repeatedNewPassword("newPassword!23@")
                .oldPassword(PASSWORD)
                .build();

        User user = User.builder().username("Test").password(passwordEncoder.encode(PASSWORD)).role(Role.ROLE_USER).id(1L).build();

        this.mockMvc.perform(post("/api/user/password").with(user(new UserPrincipal(user)))
                .content(asJsonString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenChangePasswordAsUser_givenCIncorrectChangePasswordRequest_thenReturnStatusBadRequest() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("newPassword!23@")
                .repeatedNewPassword("newPassword!23@")
                .oldPassword("incorrectOldPassword123!!")
                .build();

        User user = User.builder().username("Test").password(passwordEncoder.encode(PASSWORD)).role(Role.ROLE_USER).id(1L).build();

        this.mockMvc.perform(post("/api/user/password").with(user(new UserPrincipal(user)))
                .content(asJsonString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Incorrect old password."));
    }

    @Test
    @WithAnonymousUser
    public void whenChangePasswordWithAnonymousUser_givenCorrectChangePasswordRequest_thenReturnStatusUnathorized() throws Exception {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .newPassword("newPassword!23@")
                .repeatedNewPassword("newPassword!23@")
                .oldPassword(PASSWORD)
                .build();

        this.mockMvc.perform(post("/api/user/password")
                .content(asJsonString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


}