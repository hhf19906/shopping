package com.hhf.shopping.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.hhf.shopping.user.mapper")
public class ShoppingUserManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingUserManageApplication.class, args);
	}

}
