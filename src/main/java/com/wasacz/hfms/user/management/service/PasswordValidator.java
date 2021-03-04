package com.wasacz.hfms.user.management.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    private final static String PASSWORD_REGEX_RULES = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,30}$";

    public static boolean isPasswordMeetRules(String password)
    {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX_RULES);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
