package com.wasacz.hfms.user.authorization.controller;

import com.wasacz.hfms.user.authorization.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect login or password.")
    @ExceptionHandler({BadCredentialsException.class})
    public void handleAuthenticationException(BadCredentialsException ex) {
        log.debug("Bad credentials exception: {}", ex.getMessage());
    }

    @PostMapping("/sign")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authParam) {
        authService.authenticateUser(authParam.getUsername(), authParam.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(JwtTokenResponse.builder().token(authService.generateToken()).build());
    }
}