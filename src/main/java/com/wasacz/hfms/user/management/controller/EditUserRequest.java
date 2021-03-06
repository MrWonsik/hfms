package com.wasacz.hfms.user.management.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditUserRequest implements ValidableRequest {
    private String password;
    private Boolean isEnabled;
}
