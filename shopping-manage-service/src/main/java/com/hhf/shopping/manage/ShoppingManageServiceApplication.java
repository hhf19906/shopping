package com.hhf.shopping.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.hhf.shopping.manage.mapper")
@EnableTransactionManagement
public class ShoppingManageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingManageServiceApplication.class, args);
	}

}
