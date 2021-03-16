package com.wasacz.hfms.user.authorization.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private Authentication authentication;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    public void whenAuthenticateUser_givenUsernameAndPassword_thenSetContextAuthenticate() {
        //given
        String username = "Username";
        String password = "SuperSecurePassword!@#1";
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        //when
        authService.authenticateUser(username, password);

        //then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
    }

}