package com.wasacz.hfms.controller;

import com.wasacz.hfms.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/sign")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authParam) {
        authService.authenticateUser(authParam.getUsername(), authParam.getPassword());
        String accessToken = authService.generateToken();
        return ResponseEntity.ok(accessToken);
    }
}