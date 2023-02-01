package com.product.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication()

public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
		System.out.println("Welcome to CRUD Demo");
	}

}
