package com.hhf.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hhf.shopping")
public class ShoppingCartWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingCartWebApplication.class, args);
	}

}
