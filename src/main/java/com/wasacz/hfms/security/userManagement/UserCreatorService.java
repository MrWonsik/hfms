package com.wasacz.hfms.security.userManagement;

import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreatorService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserCreatorService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void createUser(CreateUserRequest createUserRequest) {
        //TODO: add validation!
        userRepository.save(
                User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .build());
    }
}
