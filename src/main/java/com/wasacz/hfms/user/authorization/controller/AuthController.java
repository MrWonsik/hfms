package com.wasacz.hfms.user.authorization.controller;

import com.wasacz.hfms.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    //TODO: add tests!

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authParam) {
        authService.authenticateUser(authParam.getUsername(), authParam.getPassword());
        String accessToken = authService.generateToken();
        return ResponseEntity.ok(accessToken); //TODO: works on return token (maybe in header?)
    }
}