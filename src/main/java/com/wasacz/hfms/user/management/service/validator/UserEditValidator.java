package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.EditUserRequest;
import com.wasacz.hfms.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
public class UserEditValidator implements ValidatorStrategy {

    private final UserRepository userRepository;

    public UserEditValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(Long id, Object request) {
        if (!(request instanceof EditUserRequest) || id == null) {
            String msg = "Incorrect arguments pass to validate method in EditUserRequest.class!";
            log.error(msg);
            throw new InvalidValidateMethodArguments(msg);
        }
        EditUserRequest editUserRequest = (EditUserRequest) request;
        if (Optional.ofNullable(editUserRequest.getPassword()).isPresent()) {
            UserValidator.validatePassword(editUserRequest.getPassword());
        }

        if (userRepository.findById(id).isEmpty()) {
            String msg = "User with id " + id + " not found.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
