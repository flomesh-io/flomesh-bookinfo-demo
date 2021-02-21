package com.redhat.bookinfo.details;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DetailsApplication {
    public static void main(String[] args) {
        SpringApplication.run( DetailsApplication.class, args);
    }
}
