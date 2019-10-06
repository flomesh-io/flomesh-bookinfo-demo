package com.redhat.bookinfo.reviews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;

@EnableCircuitBreaker
@EnableHystrix
@EnableHystrixDashboard
@EnableFeignClients(basePackages = "com.redhat.bookinfo.ratings")
@ComponentScan(basePackages = "com.redhat.bookinfo")
@SpringBootApplication
public class ReviewsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReviewsApplication.class, args);
	}
}