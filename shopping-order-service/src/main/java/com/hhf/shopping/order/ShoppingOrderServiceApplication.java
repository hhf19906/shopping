package com.hhf.shopping.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.hhf.shopping")
@MapperScan("com.hhf.shopping.order.mapper")
@EnableTransactionManagement
public class ShoppingOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingOrderServiceApplication.class, args);
	}

}
