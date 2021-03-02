package com.wasacz.hfms.service.userManagement;

import com.wasacz.hfms.controller.userManagement.CreateUserRequest;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j //This is used to automatically append logger to class
public class UserCreatorService {

    private final PasswordEncoder passwordEncoder;

    private final CreateUserValidator createUserValidator;

    private final UserRepository userRepository;

    public UserCreatorService(PasswordEncoder passwordEncoder, UserRepository userRepository, CreateUserValidator createUserValidator) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.createUserValidator = createUserValidator;
    }

    public void createUser(CreateUserRequest createUserRequest) {
        // TODO: add validation for creating user (check if user with username already exists then throw exception)
        createUserValidator.validate(createUserRequest);
        User createdUser = userRepository.save(buildUser(createUserRequest));
        log.debug("User {} has been created with role: {}.", createUserRequest.getUsername(), createUserRequest.getRole()); // TODO: save log to file!
    }

    private User buildUser(CreateUserRequest createUserRequest) {
        return User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .role(Role.valueOf(createUserRequest.getRole()))
                .build();
    }
}
