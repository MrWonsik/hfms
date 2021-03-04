package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.persistence.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserResponse {
    private final Long id;
    private final String username;
    private final Role role;


}
