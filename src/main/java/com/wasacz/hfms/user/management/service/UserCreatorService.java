package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCreatorService {

    private final PasswordEncoder passwordEncoder;

    private final UserCreatorValidator userCreatorValidator;

    private final UserRepository userRepository;

    public UserCreatorService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserCreatorValidator userCreatorValidator) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userCreatorValidator = userCreatorValidator;
    }

    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        userCreatorValidator.validate(createUserRequest);
        User createdUser = userRepository.save(buildUser(createUserRequest));
        log.debug("User {} has been created with role: {}.", createUserRequest.getUsername(), createUserRequest.getRole());
        return CreateUserResponse.builder()
                .id(createdUser.getId())
                .username(createdUser.getUsername())
                .role(createdUser.getRole())
                .build();
    }

    private User buildUser(CreateUserRequest createUserRequest) {
        return User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .role(Role.valueOf(createUserRequest.getRole()))
                .build();
    }
}
