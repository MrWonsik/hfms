package com.wasacz.hfms.helpers;

import com.wasacz.hfms.persistence.Role;
import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.persistence.UserRepository;
import com.wasacz.hfms.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.wasacz.hfms.helpers.UserCreatorStatic.PASSWORD;

@Service
public class CurrentUserMock {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserPrincipal createMockUser(String username, Role role) {
        User user = User.builder().username(username).password(passwordEncoder.encode(PASSWORD)).role(role).build();
        userRepository.save(user);
        return new UserPrincipal(user);
    }
}
