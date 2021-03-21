package com.wasacz.hfms.user.management.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String repeatedNewPassword;

}
