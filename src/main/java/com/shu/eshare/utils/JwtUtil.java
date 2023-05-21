package com.shu.eshare.utils;

import com.google.gson.Gson;
import com.shu.eshare.model.domain.User;
import com.shu.eshare.model.security.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 * 遗弃
 */
@Deprecated
public class JwtUtil {

    //jwt默认有效期为3个小时
    public static final Long JWT_TTL = 3*60 * 60 *1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文
    public static final String JWT_KEY = "sangeng";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 生成jtw，默认有效期JWT_TTL 3小时
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw，并自定义token过期时间
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 创建token
     * @param id 唯一id
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis 超时时间
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis==null){
            ttlMillis=JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("sg")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }




    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static void main(String[] args) throws Exception {
//        ttlTest();
        String username = "sadasd";
        String jwt = createJWT(username);

        Claims claims = parseJWT(jwt);
        System.out.println(claims);
        System.out.println(claims.getSubject());
    }

    public static void gsonTest() throws Exception {
        LoginUser loginUser = new LoginUser();
        User user = new User();
        user.setUserId(2L);
        user.setNickname("阿巴阿巴ss");
        user.setUsername("ljs叔叔");
        user.setBirthday(new Date());
        loginUser.setUser(user);
        System.out.println("origin-user:"+user);
        Gson gson = new Gson();
        String userJson = gson.toJson(loginUser);
        System.out.println("toJson:"+userJson);
        String jwt = createJWT(userJson);
        System.out.println("jwt:"+jwt);
        Claims claims = parseJWT(jwt);
        String subject = claims.getSubject();
        System.out.println("subject:"+subject);
        LoginUser fromJson = gson.fromJson(subject, LoginUser.class);
        System.out.println("fromJson:"+fromJson);
        System.out.println("claims:"+claims);
    }

    public static void parseTest() throws Exception {
        User user = new User();
        user.setUserId(2L);
        user.setNickname("阿巴阿巴ss");
        user.setUsername("ljs叔叔");
        user.setBirthday(new Date());
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        String jwt = createJWT(userJson,60 *1000L);//设置一分钟过期时间
        System.out.println(jwt);
    }

    public static Date getExpireTime(Claims claims){
        return claims.getExpiration();
    }

    public static void ttlTest() throws Exception {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3OWEzNWM4NjlmNDI0ZTFlYmZjYTExODkwMWY0NDhjZCIsInN1YiI6IntcInVzZXJJZFwiOjIsXCJ1c2VybmFtZVwiOlwibGpz5Y-U5Y-UXCIsXCJuaWNrbmFtZVwiOlwi6Zi_5be06Zi_5be0c3NcIixcImJpcnRoZGF5XCI6XCJGZWIgMjgsIDIwMjMgMTE6MTQ6MzAgUE1cIn0iLCJpc3MiOiJzZyIsImlhdCI6MTY3NzU5NzI3MSwiZXhwIjoxNjc3NTk3MzMxfQ.-pxKbAvo7Ft5TjCQ2l9rHWHQFiYyYm0c7Iw1FS5zU40";
        Claims claims = parseJWT(jwt);
        System.out.println(claims.getSubject());
        //JWT过期会抛出异常 ExpiredJwtException
    }


}
