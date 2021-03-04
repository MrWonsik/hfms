package com.wasacz.hfms.user.authorization.controller;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
public class AuthRequest {
    @NotBlank
    private final String username;

    @NotBlank
    private final String password;
}

