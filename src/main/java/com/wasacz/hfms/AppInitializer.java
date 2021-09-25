package com.wasacz.hfms;

import com.wasacz.hfms.utils.ExampleUserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppInitializer implements CommandLineRunner {

    @Value("${recreateExampleUser:false}")
    private boolean recreateExampleUser;

    private final ExampleUserFactory exampleUserFactory;

    public AppInitializer(ExampleUserFactory exampleUserFactory) {
        this.exampleUserFactory = exampleUserFactory;
    }


    @Override
    public void run(String... args) {
        exampleUserFactory.generateAdminUser();

        if (recreateExampleUser) {
            exampleUserFactory.genarateExampleUser();
        }
    }

}
