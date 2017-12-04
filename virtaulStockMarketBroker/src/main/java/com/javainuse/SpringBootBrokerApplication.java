package com.javainuse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.javainuse.controllers.TestControllerBroker;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringBootBrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBrokerApplication.class, args);
		
//		TestControllerBroker testControllerStock = new TestControllerBroker();
//		testControllerStock.scheduledJob();
	}
}
