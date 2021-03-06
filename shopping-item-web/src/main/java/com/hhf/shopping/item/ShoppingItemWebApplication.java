package com.hhf.shopping.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.hhf.shopping")
public class ShoppingItemWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingItemWebApplication.class, args);
	}

}
