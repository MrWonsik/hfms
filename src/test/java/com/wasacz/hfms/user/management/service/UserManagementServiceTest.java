package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.controller.EditUserRequest;
import com.wasacz.hfms.user.management.controller.UserResponse;
import com.wasacz.hfms.user.management.service.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    public void whenCreateUser_givenCreateUserRequest_thenSaveUser() {
        //given
        long userId = 1L;
        String username = "Test";
        String password = "secure_password";
        String role = "ROLE_USER";
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(username)
                .password(password)
                .role(role).build();

        User user = User.builder().username(username).password(password).role(Role.valueOf(role)).id(userId).build();

        when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        UserResponse userResponse = userManagementService.createUser(createUserRequest);

        //then
        assertEquals(userResponse.getUsername(), username);
        assertEquals(userResponse.getRole(), Role.valueOf(role));
        assertEquals(userResponse.getId(), userId);
    }

    @Test
    public void whenEditUser_givenEditUserRequest_thenUpdateUser() {
        //given
        long userId = 1L;

        EditUserRequest editUserRequest = EditUserRequest.builder()
                .isEnabled(false)
                .password("NewSecurePassword123!!")
                .build();

        User user = mock(User.class);
        when(userRepository.getOne(1L)).thenReturn(user);

        //when
        UserResponse userResponse = userManagementService.editUser(userId, editUserRequest);

        //then
        verify(user, times(1)).setPassword(passwordEncoder.encode("NewSecurePassword123!!"));
        verify(user, times(1)).setEnabled(false);
        assertFalse(userResponse.getIsEnabled());
    }

    @Test
    public void whenEditUser_givenEditUserRequestWithIsEnabled_thenUpdateUser() {
        //given
        long userId = 1L;

        EditUserRequest editUserRequest = EditUserRequest.builder()
                .isEnabled(true)
                .build();

        User user = mock(User.class);
        when(userRepository.getOne(1L)).thenReturn(user);

        //when
        UserResponse userResponse = userManagementService.editUser(userId, editUserRequest);

        //then
        verify(user, times(0)).setPassword(any());
        verify(user, times(1)).setEnabled(true);
        assertFalse(userResponse.getIsEnabled());
    }

    @Test
    public void whenEditUser_givenEditUserRequestWithPassword_thenUpdateUser() {
        //given
        long userId = 1L;

        EditUserRequest editUserRequest = EditUserRequest.builder()
                .password("NewSecurePassword123!!")
                .build();

        User user = mock(User.class);
        when(userRepository.getOne(1L)).thenReturn(user);

        //when
        userManagementService.editUser(userId, editUserRequest);

        //then
        verify(user, times(1)).setPassword(passwordEncoder.encode("NewSecurePassword123!!"));
        verify(user, times(0)).setEnabled(anyBoolean());
    }

    @Test
    public void whenDeleteUser_givenDeletingUserId_thenDeleteUser() {
        //given
        long userId = 1L;

        User user = User.builder()
                .username("username")
                .password("superSecurePassword123!@!")
                .role(Role.valueOf("ROLE_USER"))
                .id(userId)
                .build();
        when(userRepository.getOne(1L)).thenReturn(user);

        //when
        UserResponse userResponse = userManagementService.deleteUser(userId);

        //then
        assertEquals(userResponse.getId(), userId);
    }
}