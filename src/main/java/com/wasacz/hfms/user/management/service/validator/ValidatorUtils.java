package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.persistence.Role;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
public class ValidatorUtils {

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

    static void isFieldBlank(String field, String fieldName) {
        if (isBlank(field)) {
            String message = fieldName + " cannot be blank.";
            log.debug(message);
            throw new IllegalStateException(message);
        }
    }
}
