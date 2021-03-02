package com.wasacz.hfms.security.userManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    private String username;
    private String password;
    private String role; //TODO: enum find a way!
}
