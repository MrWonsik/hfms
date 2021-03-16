package com.wasacz.hfms.user.authorization.controller;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenResponse {
    private final String token;
}
