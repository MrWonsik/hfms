package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDeleteValidator implements ValidatorStrategy {

    private final UserRepository userRepository;

    public UserDeleteValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(Long id, Object request) {
        if (request != null || id == null) {
            String msg = "Incorrect arguments pass to validate method in UserDeleteValidator.class!";
            log.error(msg);
            throw new InvalidValidateMethodArguments(msg);
        }
        if (userRepository.findById(id).isEmpty()) {
            String msg = "User with id " + id + " not found.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
