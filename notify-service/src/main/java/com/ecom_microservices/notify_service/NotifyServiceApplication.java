package com.ecom_microservices.notify_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
@EnableScheduling
public class NotifyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifyServiceApplication.class, args);
	}

}
