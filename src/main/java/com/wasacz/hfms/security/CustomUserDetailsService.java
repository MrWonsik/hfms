package com.wasacz.hfms.security;

import com.wasacz.hfms.persistence.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserPrincipal.create(
                userRepository.findByUsername(username).orElseThrow(
                        () -> new UsernameNotFoundException("User not found with username: " + username)
                ));
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        return UserPrincipal.create(userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id)));
    }
}
