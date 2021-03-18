package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.persistence.User;
import com.wasacz.hfms.security.CurrentUser;
import com.wasacz.hfms.security.UserPrincipal;
import com.wasacz.hfms.user.management.service.ChangePasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("api/user/password")
@Slf4j
public class ChangePasswordController {

    private final ChangePasswordService passwordChangerService;

    public ChangePasswordController(ChangePasswordService passwordChangerService) {
        this.passwordChangerService = passwordChangerService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @PostMapping("")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<?> changePassword(@CurrentUser UserPrincipal user, @RequestBody ChangePasswordRequest changePasswordRequest) {
        passwordChangerService.changePassword(user.getUser(), changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
