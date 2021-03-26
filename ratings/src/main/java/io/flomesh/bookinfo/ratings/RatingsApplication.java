package io.flomesh.bookinfo.ratings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "io.flomesh.bookinfo")
@SpringBootApplication
@EnableDiscoveryClient
public class RatingsApplication {
    public static void main(String[] args) {
        SpringApplication.run( RatingsApplication.class, args);
    }
}