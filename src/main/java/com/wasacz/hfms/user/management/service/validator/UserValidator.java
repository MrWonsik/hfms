package com.wasacz.hfms.user.management.service.validator;

import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validate(Long id, Object request, ValidatorStrategy validatorStrategy) {
        validatorStrategy.validate(id, request);
    }
}
