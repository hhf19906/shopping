package com.hhf.shopping.config;

import com.alibaba.fastjson.JSON;
import com.hhf.shopping.util.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.http.Cookie;

//登录模块的拦截器
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    // 在用户进入控制器之前
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //https://www.jd.com/?newToken=eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6ImxhbnhpbmciLCJ1c2VySWQiOiIxIn0.oid_4Oke-_E7pEjeFdnqj9DjQYobf4FQmf5_mvldhtM
        String token = request.getParameter("newToken");

        if (token!=null){ // 当token 不为null的时候放cookie
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }
        // 当用户访问非登录之后的页面，登录之后继续访问其他业务模块时，url 并没有newToken，但是后台可能将token 放入了cookie 中
        if (token==null){
            token = CookieUtil.getCookieValue(request,"token",false);
        }
        // 从cookie 中获取token，解密token
        if (token!=null){
            // 开始解密token 并获取nickName
            Map map = getUserMapByToken(token);
            // 取出用户昵称
            String nickName = (String) map.get("nickName");
            // 保存到作用域
            request.setAttribute("nickName",nickName);
        }

        // 在拦截器中获取方法上的注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取方法上的注解LoginRequire
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (methodAnnotation!=null){
            // 判断用户是否登录了 调用verify
            // 获取服务器上的ip 地址
            String salt = request.getHeader("X-forwarded-for");
            //String salt = request.getHeader("localhost");
            // 调用verify（）认证
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&salt=" + salt);
            if ("success".equals(result)){
                // 登录，认证成功！
                // 保存一下userId
                // 开始解密token 获取nickName
                Map map = getUserMapByToken(token);
                // 取出userId
                String userId = (String) map.get("userId");
                // 保存到作用域
                request.setAttribute("userId",userId);
                return true;
            }else {
                // 认证失败！并且 methodAnnotation.autoRedirect()=true; 必须登录
                if (methodAnnotation.autoRedirect()){
                    // 必须登录！跳转到页面！
                    // 先获取到url
                    String requestURL  = request.getRequestURL().toString();
                    System.out.println("requestURL:"+requestURL);
                    // 将url 进行转换
                    String encodeURL  = URLEncoder.encode(requestURL, "UTF-8");
                    System.out.println("encodeURL："+encodeURL);

                    response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                    return false;
                }
            }
        }

        return true;
    }
    // 解密token获取map数据
    private Map getUserMapByToken(String token) {
        // 获取token中间部分的数据
        String tokenUserInfo  = StringUtils.substringBetween(token, ".");
        // 将tokenUserInfo 进行base64解码
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        // 解码之后得到byte数组
        byte[] decode = base64UrlCodec.decode(tokenUserInfo);
        // 需要先将decode 转成String
        String mapJson =null;
        try {
            mapJson = new String(decode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 将字符串转换为map 直接返回！
        return JSON.parseObject(mapJson,Map.class);

    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


}
