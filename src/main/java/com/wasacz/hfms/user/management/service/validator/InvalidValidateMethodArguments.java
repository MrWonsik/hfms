package com.wasacz.hfms.user.management.service.validator;

public class InvalidValidateMethodArguments extends RuntimeException {
    public InvalidValidateMethodArguments(String errorMessage) {
        super(errorMessage);
    }
}
