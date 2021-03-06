package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.user.management.service.UserManagementService;
import com.wasacz.hfms.user.management.service.validator.InvalidValidateMethodArguments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleValidationExceptions(
            RuntimeException ex) {
        log.debug("API layer validation errors: {}", ex.getMessage());
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({InvalidValidateMethodArguments.class})
    public String handleServerErrors() {
        return "Internal server error!";
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
