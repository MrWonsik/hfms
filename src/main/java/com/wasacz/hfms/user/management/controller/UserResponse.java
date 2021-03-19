package com.wasacz.hfms.user.management.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wasacz.hfms.persistence.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    private final Long id;
    private final String username;
    private final Boolean isEnabled;
    private final Role role;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate createDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate updateDate;
}
