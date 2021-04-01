package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.user.management.controller.ChangePasswordRequest;
import com.wasacz.hfms.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChangePasswordValidator {

    private final PasswordEncoder passwordEncoder;

    public ChangePasswordValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void validate(String codedOldPassword, ChangePasswordRequest changePasswordRequest) {
        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), codedOldPassword)){
            String msg = "Incorrect old password.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        };

        UserValidator.isPasswordEquals(changePasswordRequest.getNewPassword(), changePasswordRequest.getRepeatedNewPassword());
        UserValidator.isPasswordNotEquals(changePasswordRequest.getNewPassword(), changePasswordRequest.getOldPassword());
        UserValidator.validatePassword(changePasswordRequest.getNewPassword());
    }
}
