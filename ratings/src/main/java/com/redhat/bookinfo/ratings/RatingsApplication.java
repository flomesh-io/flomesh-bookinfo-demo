package com.redhat.bookinfo.ratings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.redhat.bookinfo")
@SpringBootApplication
public class RatingsApplication {
    public static void main(String[] args) {
        SpringApplication.run( RatingsApplication.class, args);
    }
}