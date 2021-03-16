package com.wasacz.hfms;

import com.wasacz.hfms.user.management.controller.CreateUserRequest;
import com.wasacz.hfms.user.management.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppInitializer implements CommandLineRunner {

    @Value("${spring.jpa.hibernate.ddl-auto:\"\"}")
    private String ddlAuto;

    private final UserManagementService userManagementService;

    public AppInitializer(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }


    @Override
    public void run(String... args) {
        if (ddlAuto.equals("create")) {
            userManagementService.createUser(CreateUserRequest.builder().username("admin").password("Admin123!@").role("ROLE_ADMIN").build());
            userManagementService.createUser(CreateUserRequest.builder().username("user").password("User123!@").role("ROLE_USER").build());
        }
    }
}
