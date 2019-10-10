package com.hhf.shopping.passport;

import com.hhf.shopping.passport.config.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShoppingPassportWebApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testJWT(){
		String key = "hhf";
		HashMap<String, Object> map = new HashMap<>();
		map.put("userId",1001);
		map.put("nickName","admin");
		String salt = "localhost";
		String token = JwtUtil.encode(key, map, salt);
		System.out.println("token:"+token);
		//eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6ImFkbWluIiwidXNlcklkIjoxMDAxfQ.RRnQn9W-Q-kdU1kezYTy4oWi1dgT9LtH0Cq0HMHO94U

		Map<String, Object> map1 = JwtUtil.decode(token, key, salt);
		System.out.println(map1);
	}
}
