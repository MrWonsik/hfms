package com.wasacz.hfms.utils;

import lombok.extern.slf4j.Slf4j;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Slf4j
public class ValidatorUtils {

    public static void handleFieldBlank(String field, String fieldName) {
        if (isBlank(field)) {
            String message = "Field: " + fieldName + " cannot be blank.";
            log.debug(message);
            throw new IllegalStateException(message);
        }
    }

    public static void handleFieldIsNull(Object transactionDate, String fieldName) {
        if (transactionDate == null) {
            String message = "Field " + fieldName + " is incorrect.";
            log.debug(message);
            throw new IllegalStateException(message);
        }
    }
}
