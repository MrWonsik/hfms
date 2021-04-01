package com.wasacz.hfms.user.management.service.validator;

import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserCreateValidator implements ValidatorStrategy {

    private final UserRepository userRepository;

    public UserCreateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(Long id, Object request) {
        if (!(request instanceof CreateUserRequest) || id != null) {
            String msg = "Incorrect arguments pass to validate method in UserCreatorValidator.class!";
            log.error(msg);
            throw new InvalidValidateMethodArguments(msg);
        }
        CreateUserRequest createUserRequest = (CreateUserRequest) request;
        ValidatorUtils.isFieldBlank(createUserRequest.getUsername(), "Username");
        UserValidator.validatePassword(createUserRequest.getPassword());
        UserValidator.validateRole(createUserRequest.getRole());

        if (userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
            String msg = "Username is already used " + createUserRequest.getUsername() + "!";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
