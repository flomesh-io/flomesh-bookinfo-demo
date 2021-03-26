package io.flomesh.bookinfo.reviews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableCircuitBreaker
@EnableHystrix
@EnableFeignClients(basePackages = "io.flomesh.bookinfo.ratings")
@ComponentScan(basePackages = "io.flomesh.bookinfo")
@SpringBootApplication
@EnableDiscoveryClient
public class ReviewsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReviewsApplication.class, args);
	}
}