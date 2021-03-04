package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCreatorValidatorTest {

    private final String CORRECT_PASSWORD = "SuperTajneHaslo123@@@";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCreatorValidator userCreatorValidator;

    @Test
    public void whenValidateCreateUserRequest_givenAllCorrectFields_thenNoThrowException() {
        //given
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Correct username");
        createUserRequest.setPassword(CORRECT_PASSWORD);
        createUserRequest.setRole("ROLE_USER");
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.empty());

        //when
        Assertions.assertDoesNotThrow(() -> userCreatorValidator.validate(createUserRequest));
    }

    @Test
    public void whenValidateCreateUserRequest_givenAllCorrectFieldsWithAlreadyUserUsername_thenThrowException() {
        //given
        User user = mock(User.class);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username_that_already_used");
        createUserRequest.setPassword(CORRECT_PASSWORD);
        createUserRequest.setRole("ROLE_USER");
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.of(user));

        //when and then
        Assertions.assertThrows(IllegalArgumentException.class, () -> userCreatorValidator.validate(createUserRequest), "Username is already used username_that_already_used!");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void whenValdateCreateUserRequest_givenBlankUsername_thenThrowException(String username) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(CORRECT_PASSWORD);
        createUserRequest.setRole("ROLE_USER");

        Assertions.assertThrows(IllegalStateException.class, () -> userCreatorValidator.validate(createUserRequest), "Username cannot be blank.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void whenValdateCreateUserRequest_givenBlankPassword_thenThrowException(String password) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Username");
        createUserRequest.setPassword(password);
        createUserRequest.setRole("ROLE_USER");

        Assertions.assertThrows(IllegalStateException.class, () -> userCreatorValidator.validate(createUserRequest), "Password cannot be blank.");
    }

    @Test
    public void whenValdateCreateUserRequest_givenPasswordThatNotMeetTheRules_thenThrowException() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Username");
        createUserRequest.setPassword("nie poprawne haslo");
        createUserRequest.setRole("ROLE_USER");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userCreatorValidator.validate(createUserRequest), "Password don't meet rules.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void whenValdateCreateUserRequest_givenBlankRole_thenThrowException(String role) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword(CORRECT_PASSWORD);
        createUserRequest.setRole(role);

        Assertions.assertThrows(IllegalStateException.class, () -> userCreatorValidator.validate(createUserRequest), "Role cannot be blank.");
    }

    @Test
    public void whenValdateCreateUserRequest_givenRoleThatNotExists_thenThrowException() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Username");
        createUserRequest.setPassword(CORRECT_PASSWORD);
        createUserRequest.setRole("ROLE_THAT_WILL_NEVER_EXISTS");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userCreatorValidator.validate(createUserRequest), "Provided incorrect role.");
    }

}