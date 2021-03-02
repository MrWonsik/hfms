package com.wasacz.hfms.security.userManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserCreatorController {

    @Autowired
    private UserCreatorService userCreatorService;

    @PostMapping("")
    public ResponseEntity createUser(@RequestBody CreateUserRequest user) {
        //TODO: logger, access only for admin, validation!
        userCreatorService.createUser(user);
        return ResponseEntity.ok().build();
    }
}
