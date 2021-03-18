package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.user.management.controller.ChangePasswordRequest;
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

        ValidatorUtils.isPasswordEquals(changePasswordRequest.getNewPassword(), changePasswordRequest.getRepeatedNewPassword());
        ValidatorUtils.isPasswordNotEquals(changePasswordRequest.getNewPassword(), changePasswordRequest.getOldPassword());
        ValidatorUtils.validatePassword(changePasswordRequest.getNewPassword());
    }
}
