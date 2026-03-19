package com.example.kripta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KriptaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KriptaApplication.class, args);
    }
}
