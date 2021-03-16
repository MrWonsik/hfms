package com.wasacz.hfms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = {
        HfmsApplication.class,
        Jsr310JpaConverters.class})
public class HfmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HfmsApplication.class, args);
    }

}
