package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.user.management.service.UserManagementService;
import com.wasacz.hfms.user.management.service.validator.InvalidValidateMethodArguments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public void handleValidationExceptions(RuntimeException ex, HttpServletResponse response) throws IOException {
        log.debug("API layer validation errors: {}", ex.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal server error!")
    @ExceptionHandler({InvalidValidateMethodArguments.class})
    public void handleServerErrors() {}

    @GetMapping("")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userManagementService.getAllUsers());
    }

    @PostMapping("")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
        UserResponse userResponse = userManagementService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> editUser(@RequestBody EditUserRequest user, @PathVariable Long id) {
        UserResponse userResponse = userManagementService.editUser(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        UserResponse userResponse = userManagementService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
