package com.wasacz.hfms.user.management.controller;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateUserRequest {

    private String username;
    private String password;
    private String role;
}
