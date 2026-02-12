package com.nearShop.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@RestController
public class NearShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(NearShopApplication.class, args);
    
	}

	@GetMapping("/")
    	public String testApi() {
        return "Spring Boot is running successfully!";
		}

}
