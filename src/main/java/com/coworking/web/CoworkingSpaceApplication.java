package com.coworking.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.coworking")
public class CoworkingSpaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoworkingSpaceApplication.class, args);
    }
}
