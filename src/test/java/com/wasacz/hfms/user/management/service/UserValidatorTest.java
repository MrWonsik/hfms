package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.controller.EditUserRequest;
import com.wasacz.hfms.user.management.service.validator.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.wasacz.hfms.helpers.UserCreatorStatic.PASSWORD;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void whenValidateCreateUserRequest_givenAllCorrectFields_thenNoThrowException() {
        //given
        CreateUserRequest createUserRequest = CreateUserRequest.builder().username("Username").password(PASSWORD).role("ROLE_USER").build();
        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.empty());

        //when
        Assertions.assertDoesNotThrow(() -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository)));
    }

    @Test
    public void whenValidateCreateUserRequest_givenAllCorrectFieldsWithAlreadyUserUsername_thenThrowException() {
        //given
        User user = mock(User.class);

        CreateUserRequest createUserRequest = CreateUserRequest.builder().username("username_that_already_used").password(PASSWORD).role("ROLE_USER").build();

        when(userRepository.findByUsername(createUserRequest.getUsername())).thenReturn(Optional.of(user));

        //when and then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository))
                , "Username is already used username_that_already_used!"
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void whenValidateCreateUserRequest_givenBlankUsername_thenThrowException(String username) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder().username(username).password(PASSWORD).role("ROLE_USER").build();


        Assertions.assertThrows(IllegalStateException.class,
                () -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository)),
                "Username cannot be blank.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void whenValidateCreateUserRequest_givenBlankPassword_thenThrowException(String password) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder().username("Username").password(password).role("ROLE_USER").build();


        Assertions.assertThrows(IllegalStateException.class, () -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository)),
                "Password cannot be blank.");
    }

    @Test
    public void whenValidateCreateUserRequest_givenPasswordThatNotMeetTheRules_thenThrowException() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder().username("Username").password("nie poprawne haslo").role("ROLE_USER").build();


        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository)),
                "Password don't meet rules.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void whenValidateCreateUserRequest_givenBlankRole_thenThrowException(String role) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder().username("Username").password(PASSWORD).role(role).build();


        Assertions.assertThrows(IllegalStateException.class, () -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository)),
                "Role cannot be blank.");
    }

    @Test
    public void whenValidateCreateUserRequest_givenRoleThatNotExists_thenThrowException() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder().username("Username").password(PASSWORD).role("ROLE_THAT_NOT_EXISTS").build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository)),
                "Provided incorrect role.");
    }

    @Test
    public void whenValidateCreateUserRequest_givenNullCreateUserRequest_thenThrowException() {
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(1L, null, new UserCreateValidator(userRepository))
                , "Incorrect arguments pass to validate method in CreateUserRequest.class!");
    }

    @Test
    public void whenValidateCreateUserRequest_givenNullCreateUserRequestAndNullUserId_thenThrowException() {
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(null, null, new UserCreateValidator(userRepository))
                , "Incorrect arguments pass to validate method in CreateUserRequest.class!");
    }

    @Test
    public void whenValidateEditUserRequest_givenAllCorrectFields_thenNoThrowException() {
        //given
        long userId = 1L;
        User user = User.builder().username("Username").password(PASSWORD).role(Role.ROLE_USER).id(userId).build();
        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).password(PASSWORD).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        Assertions.assertDoesNotThrow(() -> userValidator.validate(user.getId(), editUserRequest, new UserEditValidator(userRepository)));
    }

    @Test
    public void whenValidateEditUserRequest_givenIsEnabledField_thenNoThrowException() {
        //given
        long userId = 1L;
        User user = User.builder().username("Username").password(PASSWORD).role(Role.ROLE_USER).id(userId).build();
        EditUserRequest editUserRequest = EditUserRequest.builder().isEnabled(false).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        Assertions.assertDoesNotThrow(() -> userValidator.validate(user.getId(), editUserRequest, new UserEditValidator(userRepository)));
    }

    @Test
    public void whenValidateEditUserRequest_givenPasswordField_thenNoThrowException() {
        //given
        long userId = 1L;
        User user = User.builder().username("Username").password(PASSWORD).role(Role.ROLE_USER).id(userId).build();
        EditUserRequest editUserRequest = EditUserRequest.builder().password(PASSWORD).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        Assertions.assertDoesNotThrow(() -> userValidator.validate(user.getId(), editUserRequest, new UserEditValidator(userRepository)));
    }

    @Test
    public void whenValidateEditUserRequest_givenEmptyRequest_thenNoThrowException() {
        //given
        long userId = 1L;
        User user = User.builder().username("Username").password(PASSWORD).role(Role.ROLE_USER).id(userId).build();
        EditUserRequest editUserRequest = EditUserRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when
        Assertions.assertDoesNotThrow(() -> userValidator.validate(user.getId(), editUserRequest, new UserEditValidator(userRepository)));
    }

    @Test
    public void whenValidateEditUserRequest_givenNullUserId_thenThrowException() {
        //given
        EditUserRequest editUserRequest = EditUserRequest.builder().build();

        //when
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(null, editUserRequest, new UserEditValidator(userRepository))
                , "Incorrect arguments pass to validate method in EditUserRequest.class!");
    }

    @Test
    public void whenValidateEditUserRequest_givenNullEditUserRequest_thenThrowException() {
        //given
        User user = User.builder().username("Username").password(PASSWORD).role(Role.ROLE_USER).id(1L).build();

        //when
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(user.getId(), null, new UserEditValidator(userRepository))
                , "Incorrect arguments pass to validate method in EditUserRequest.class!");
    }

    @Test
    public void whenValidateEditUserRequest_givenNullEditUserRequestAndNullUserId_thenThrowException() {
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(null, null, new UserEditValidator(userRepository))
                , "Incorrect arguments pass to validate method in EditUserRequest.class!");
    }

    @Test
    public void whenValidateEditUserRequest_givenAndNullUserId_thenThrowException() {
        //given
        EditUserRequest editUserRequest = EditUserRequest.builder().build();

        //when
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(null, editUserRequest, new UserEditValidator(userRepository))
                , "Incorrect arguments pass to validate method in EditUserRequest.class!");
    }


    @Test
    public void whenValidateDeleteUser_givenNullUserRequestAndNullUserId_thenThrowException() {
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(null, null, new UserDeleteValidator(userRepository))
                , "Incorrect arguments pass to validate method in UserDeleteValidator.class!");
    }

    @Test
    public void whenValidateDeleteUser_givenNullUserId_thenThrowException() {
        //given
        EditUserRequest editUserRequest = EditUserRequest.builder().build();

        //when
        Assertions.assertThrows(InvalidValidateMethodArguments.class,
                () -> userValidator.validate(null, editUserRequest, new UserDeleteValidator(userRepository))
                , "Incorrect arguments pass to validate method iadadn UserDeleteValidator.class!");
    }
}