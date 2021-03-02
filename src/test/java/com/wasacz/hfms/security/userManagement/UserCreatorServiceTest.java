package com.wasacz.hfms.security.userManagement;

import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.controller.userManagement.CreateUserRequest;
import com.wasacz.hfms.service.userManagement.CreateUserValidator;
import com.wasacz.hfms.service.userManagement.UserCreatorService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreatorServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreateUserValidator createUserValidator;

    @Mock
    private Logger logger;

    @InjectMocks
    private UserCreatorService userCreatorService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    public void whenCreateUser_givenCreateUserRequest_thenSaveUser() {
        //given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Test");
        createUserRequest.setPassword("secure_password");
        createUserRequest.setRole("USER");

        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");

        //when
        userCreatorService.createUser(createUserRequest);

        //then
        verify(userRepository).save(userArgumentCaptor.capture());
        assertEquals(userArgumentCaptor.getValue().getPassword(), "encodedPassword");
        assertEquals(userArgumentCaptor.getValue().getUsername(), createUserRequest.getUsername());
        assertEquals(userArgumentCaptor.getValue().getRole().name(), createUserRequest.getRole());
    }

    @Test
    @Disabled
    public void whenCreateUser_givenCreateUserRequestWithoutRole_thenThrowException() {
        // TODO: fix this test!

        //given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Test");
        createUserRequest.setPassword("secure_password");
        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");

        //when
        userCreatorService.createUser(createUserRequest);

        //then
        //throw exception that role is mandatory
    }
}