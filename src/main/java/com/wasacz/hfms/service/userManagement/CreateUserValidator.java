package com.wasacz.hfms.service.userManagement;

import com.wasacz.hfms.controller.userManagement.CreateUserRequest;
import com.wasacz.hfms.persistence.UserRepository;
import org.springframework.stereotype.Component;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Component
public class CreateUserValidator {

    private final UserRepository userRepository;

    public CreateUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void validate(CreateUserRequest createUserRequest) {
        //TODO: add throws exception!
        if (isBlank(createUserRequest.getUsername())) {
        }

        if (isBlank(createUserRequest.getPassword())) {
        }

        if (isBlank(createUserRequest.getRole())) {
        }

        if(userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
        }
    }
}
