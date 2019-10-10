package com.hhf.shopping.passport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hhf.shopping")
public class ShoppingPassportWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingPassportWebApplication.class, args);
	}

}
