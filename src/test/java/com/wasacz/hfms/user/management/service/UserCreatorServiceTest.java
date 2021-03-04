package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.controller.CreateUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCreatorServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCreatorValidator userCreatorValidator;

    @InjectMocks
    private UserCreatorService userCreatorService;

    @Test
    public void whenCreateUser_givenCreateUserRequest_thenSaveUser() {
        //given
        long userId = 1L;
        String username = "Test";
        String password = "secure_password";
        String role = "ROLE_USER";
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setRole(role);

        User user = User.builder().username(username).password(password).role(Role.valueOf(role)).id(userId).build();

        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        CreateUserResponse userResponse = userCreatorService.createUser(createUserRequest);

        //then
        assertEquals(userResponse.getUsername(), username);
        assertEquals(userResponse.getRole(), Role.valueOf(role));
        assertEquals(userResponse.getId(), userId);
    }
}