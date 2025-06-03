package com.ecom.payment.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentserviceApplicationTests {

	@Autowired
	private RestTemplate restTemplate;

	@Test
	void contextLoads() {
		// This test ensures the Spring application context loads successfully.
		assertNotNull(restTemplate, "RestTemplate bean should be initialized");
	}
}