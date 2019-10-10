package com.hhf.shopping.passport.config;

import io.jsonwebtoken.*;

import java.util.Map;

public class JwtUtil {

    /**
     * encode ：生成token的方法
     * @param key 公共部分的jwt
     * @param param 私有部分
     * @param salt 签名部分
     * @return
     */

    public static String encode(String key,Map<String,Object> param,String salt){
        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);

        jwtBuilder = jwtBuilder.setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }

    /**
     * decode 解析token的方法
     * @param token 生成的token
     * @param key  公共部分
     * @param salt 私有的部分
     * @return
     */

    public  static Map<String,Object> decode(String token , String key, String salt){
        Claims claims=null;
        if (salt!=null){
            key+=salt;
        }
        try {
            claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch ( JwtException e) {
            return null;
        }
        return  claims;
    }

}
