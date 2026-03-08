package com.nlizzard.horizonhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class HorizonHubWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HorizonHubWebApplication.class,args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, HorizonHub!";
    }
}