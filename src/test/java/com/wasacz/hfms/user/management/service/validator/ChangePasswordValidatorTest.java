package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.user.management.controller.ChangePasswordRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.wasacz.hfms.helpers.UserCreatorStatic.PASSWORD;

@ExtendWith(MockitoExtension.class)
class ChangePasswordValidatorTest {

    private PasswordEncoder passwordEncoder;

    private ChangePasswordValidator changePasswordValidator;

    @BeforeEach
    public void init() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.changePasswordValidator = new ChangePasswordValidator(this.passwordEncoder);
    }

    @Test
    public void whenValidateChangePassword_givenAllCorrectFields_thenNoThrowException() {
        //given
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder().oldPassword(PASSWORD).newPassword("NewPassword!@#1").repeatedNewPassword("NewPassword!@#1").build();

        //when
        Assertions.assertDoesNotThrow(() -> changePasswordValidator.validate(passwordEncoder.encode(PASSWORD), changePasswordRequest));
    }

    @Test
    public void whenValidateChangePassword_givenIncorrectOldPassword_thenThrowIllegalArgumentException() {
        //given
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder().oldPassword("incorrectOldPassword123!").newPassword("NewPassword!@#1").repeatedNewPassword("NewPassword!@#1").build();

        //when
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> changePasswordValidator.validate(passwordEncoder.encode(PASSWORD), changePasswordRequest),
                "Incorrect old password.");
    }

    @Test
    public void whenValidateChangePassword_givenDifferentNewPasswordAndRepetedNewPassword_thenThrowIllegalArgumentException() {
        //given
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder().oldPassword(PASSWORD).newPassword("NewPassword!@#1").repeatedNewPassword("OtherNwePassword123!").build();

        //when
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> changePasswordValidator.validate(passwordEncoder.encode(PASSWORD), changePasswordRequest),
                "Passwords do not match.");
    }


    @Test
    public void whenValidateChangePassword_givenTheSameOldAndNewPassword_thenThrowIllegalArgumentException() {
        //given
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder().oldPassword(PASSWORD).newPassword(PASSWORD).repeatedNewPassword(PASSWORD).build();

        //when
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> changePasswordValidator.validate(passwordEncoder.encode(PASSWORD), changePasswordRequest),
                "New password cannot be the same as the old password.");
    }
}