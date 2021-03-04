package com.wasacz.hfms.user.management.controller;

import com.wasacz.hfms.user.management.service.UserCreatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserCreatorController {

    private final UserCreatorService userCreatorService;

    public UserCreatorController(UserCreatorService userCreatorService) {
        this.userCreatorService = userCreatorService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleValidationExceptions(
            RuntimeException ex) {
        log.debug("API layer validation errors: {}", ex.getMessage());
        return ex.getMessage();
    }

    @PostMapping("")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
        CreateUserResponse userResponse = userCreatorService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
