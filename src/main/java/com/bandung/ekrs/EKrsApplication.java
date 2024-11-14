package com.bandung.ekrs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class EKrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EKrsApplication.class, args);
	}

}
