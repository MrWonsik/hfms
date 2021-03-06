package com.wasacz.hfms.user.management.service.validator;

public interface ValidatorStrategy {
    void validate(Long id, Object request);
}
