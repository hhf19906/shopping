package com.hhf.shopping.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.hhf.shopping.user.mapper")
@ComponentScan("com.hhf.shopping")
public class ShoppingUserManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingUserManageApplication.class, args);
	}

}
