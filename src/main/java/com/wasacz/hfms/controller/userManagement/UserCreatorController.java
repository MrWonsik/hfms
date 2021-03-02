package com.wasacz.hfms.controller.userManagement;

import com.wasacz.hfms.service.userManagement.UserCreatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserCreatorController {

    private final UserCreatorService userCreatorService;

    public UserCreatorController(UserCreatorService userCreatorService) {
        this.userCreatorService = userCreatorService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @PostMapping("")
    @Secured("{ADMIN}")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest user) {
        userCreatorService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<?> someGet() {
        return ResponseEntity.ok().body("test");
    }
}
