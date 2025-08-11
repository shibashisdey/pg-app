package com.pgfinder.config;

import com.pgfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Create hardcoded admin user if it doesn't exist
        userService.createHardcodedAdmin();
        System.out.println("=== PG Finder Application Started Successfully ===");
        System.out.println("Default Admin Credentials:");
        System.out.println("Email: admin@pgfinder.com");
        System.out.println("Password: admin123");
        System.out.println("================================================");
    }
}
