package com.wasacz.hfms.user.management.service;

import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.user.management.controller.ChangePasswordRequest;
import com.wasacz.hfms.user.management.service.validator.ChangePasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final ChangePasswordValidator changePasswordValidator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(ChangePasswordValidator changePasswordValidator, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.changePasswordValidator = changePasswordValidator;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {
        changePasswordValidator.validate(user.getPassword(), changePasswordRequest);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

    }
}
