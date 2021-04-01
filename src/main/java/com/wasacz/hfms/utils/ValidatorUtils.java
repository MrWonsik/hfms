package com.wasacz.hfms.utils;

import com.wasacz.hfms.persistence.Role;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
public class ValidatorUtils {

    public static void isFieldBlank(String field, String fieldName) {
        if (isBlank(field)) {
            String message = fieldName + " cannot be blank.";
            log.debug(message);
            throw new IllegalStateException(message);
        }
    }
}
