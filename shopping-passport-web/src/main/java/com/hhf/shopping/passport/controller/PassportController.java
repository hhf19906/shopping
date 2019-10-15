package com.hhf.shopping.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hhf.shopping.bean.UserInfo;
import com.hhf.shopping.passport.config.JwtUtil;
import com.hhf.shopping.service.UserService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
public class PassportController {

    @Value("${token.key}")
    String key;

    @Reference
    UserService userService;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, javax.servlet.http.HttpServletRequest  request){

        String salt = "localhost";
//      String salt = request.getHeader("X-forwarded-for");
        UserInfo info = userService.login(userInfo);
      if (info !=null){ //登录成功,返回相应的token
          //生成相应的token
          HashMap<String, Object> map = new HashMap<>();
          map.put("userId",info.getId());
          map.put("nickName",info.getNickName());
          String token = JwtUtil.encode(key, map, salt);
          return token;
      }else {
          return "fail"; //登录失败
      }
    }

    //用户认证中心
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest  request){
        //String salt = request.getHeader("X-forwarded-for");
        String salt = "localhost";
        String token = request.getParameter("token");
//        String salt = request.getParameter("salt");
        Map<String, Object> map = JwtUtil.decode(token, key, salt);
        if (map!=null&&map.size()>0){
            String userId = (String) map.get("userId");
           UserInfo userInfo = userService.verify(userId);
           if (userInfo!=null){
               return "success";
           }else {
               return "fail";
           }
        }
        return "fail";
    }
}
