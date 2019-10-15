package com.hhf.shopping.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.hhf.shopping")
@MapperScan("com.hhf.shopping.payment.mapper")
public class ShoppingPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingPaymentApplication.class, args);
	}

}
