package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
@Slf4j
public class UserCreatorValidator {

    private final UserRepository userRepository;

    public UserCreatorValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(CreateUserRequest createUserRequest) {
        isFieldBlank(createUserRequest.getUsername(), "Username");
        validatePassword(createUserRequest.getPassword());
        validateRole(createUserRequest.getRole());

        if(userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
            String msg = "Username is already used " + createUserRequest.getUsername() + "!";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private void validatePassword(String password) {
        isFieldBlank(password, "Password");
        if(!PasswordValidator.isPasswordMeetRules(password)) {
            String msg = "Password don't meet rules.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private void validateRole(String role) {
        isFieldBlank(role, "Role");

        if(!EnumUtils.isValidEnum(Role.class, role)) {
            String msg = "Provided incorrect role.";
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private void isFieldBlank(String field, String fieldName) {
        if (isBlank(field)) {
            String message = fieldName + " cannot be blank.";
            log.debug(message);
            throw new IllegalStateException(message);
        }
    }
}
