package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.persistence.Role;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wasacz.hfms.utils.ValidatorUtils.isFieldBlank;

@Component
@Slf4j
public class UserValidator {

    public void validate(Long id, Object request, ValidatorStrategy validatorStrategy) {
        validatorStrategy.validate(id, request);
    }

    private final static String PASSWORD_REGEX_RULES = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$";

    static void validatePassword(String password) {
        isFieldBlank(password, "Password");
        if (!isPasswordMeetRules(password)) {
            String msg = "Password don't meet rules.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    static void isPasswordEquals(String newPassword, String repeatNewPassword) {
        if(!newPassword.equals(repeatNewPassword)) {
            String msg = "Passwords do not match.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    static void isPasswordNotEquals(String newPassword, String oldPassword) {
        if(newPassword.equals(oldPassword)) {
            String msg = "New password cannot be the same as the old password.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private static boolean isPasswordMeetRules(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX_RULES);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    static void validateRole(String role) {
        isFieldBlank(role, "Role");

        if (!EnumUtils.isValidEnum(Role.class, role)) {
            String msg = "Provided incorrect role.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
