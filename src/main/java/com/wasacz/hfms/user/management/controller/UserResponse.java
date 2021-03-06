package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.persistence.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    private final Long id;
    private final String username;
    private final Boolean isEnabled;
    private final Role role;
}
