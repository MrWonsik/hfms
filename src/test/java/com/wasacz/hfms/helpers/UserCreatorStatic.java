package com.wasacz.hfms.helpers;

import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.wasacz.hfms.helpers.ObjectMapperStatic.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserCreatorStatic {

    public static final String PASSWORD = "Password1@";

    public static MvcResult callCreateUserEndpoint(MockMvc mockMvc, String username) throws Exception {
        return mockMvc.perform(post("/api/user")
                .content(asJsonString(getCreateUserRequest(username)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public static CreateUserRequest getCreateUserRequest(String username) {
        return CreateUserRequest.builder().username(username).password(PASSWORD).role("ROLE_USER").build();
    }

}
