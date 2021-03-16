package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.controller.EditUserRequest;
import com.wasacz.hfms.user.management.controller.UserResponse;
import com.wasacz.hfms.user.management.service.validator.UserCreateValidator;
import com.wasacz.hfms.user.management.service.validator.UserDeleteValidator;
import com.wasacz.hfms.user.management.service.validator.UserEditValidator;
import com.wasacz.hfms.user.management.service.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserManagementService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserManagementService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserValidator userValidator) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        userValidator.validate(null, createUserRequest, new UserCreateValidator(userRepository));
        User createdUser = userRepository.save(buildUser(createUserRequest));
        log.debug("User {} has been created with role: {}.", createUserRequest.getUsername(), createUserRequest.getRole());
        return buildUserResponse(createdUser);
    }

    private User buildUser(CreateUserRequest createUserRequest) {
        return User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .role(Role.valueOf(createUserRequest.getRole()))
                .build();
    }

    public UserResponse editUser(Long id, EditUserRequest editUserRequest) {
        userValidator.validate(id, editUserRequest, new UserEditValidator(userRepository));
        User userToUpdate = userRepository.getOne(id);
        if (Optional.ofNullable(editUserRequest.getPassword()).isPresent()) {
            userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));
        }
        if (Optional.ofNullable(editUserRequest.getIsEnabled()).isPresent()) {
            userToUpdate.setEnabled(editUserRequest.getIsEnabled());
        }
        userRepository.save(userToUpdate);
        log.debug("User {} has been edited.", userToUpdate.getUsername());

        return buildUserResponse(userToUpdate);
    }

    public UserResponse deleteUser(Long id) {
        userValidator.validate(id, null, new UserDeleteValidator(userRepository));
        User userToDelete = userRepository.getOne(id);
        userRepository.delete(userToDelete);

        return buildUserResponse(userToDelete);
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .isEnabled(user.isEnabled())
                .build();
    }

}
