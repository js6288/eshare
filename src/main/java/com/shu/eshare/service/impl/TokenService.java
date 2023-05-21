package com.shu.eshare.service.impl;

import com.google.gson.Gson;
import com.shu.eshare.common.ErrorCode;
import com.shu.eshare.exception.BusinessException;
import com.shu.eshare.model.response.Token;
import com.shu.eshare.model.security.LoginUser;
import com.shu.eshare.utils.UUID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenService {

    // access_token令牌自定义标识
    @Value("${token.access-token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // access_token令牌有效期（默认30分钟）,单位时间分钟
    @Value("${token.access-token.expireTime}")
    private int expireTime;

    @Value("${token.refresh-token.header}")
    private String header1;

    @Value("${token.refresh-token.expireTime}")
    private int expireTime1;

    @Resource
    private UserDetailsService userDetailsService;

    //1秒
    protected static final long MILLIS_SECOND = 1000;

    //1分钟
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    //20分钟
    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    public Token createToken(String username){
        //将LoginUser转成gson
        Gson gson = new Gson();
        //创建jwt,将username存入jwt,设置过期时间expire-time分钟
        String accessToken = createJWT(username, MILLIS_MINUTE * expireTime);
        String refreshToken = createJWT(username, MILLIS_MINUTE * expireTime1);
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        return token;
    }

    /**
     * 重新生成新的access_token和新的refresh_token
     * @param request
     * @return
     */
    public Token refreshToken(HttpServletRequest request){
        String refreshToken = getRefreshToken(request);
        if (StringUtils.isNotEmpty(refreshToken)){

            Claims claims;
            try{
                claims = parseJWT(refreshToken);
                String username = claims.getSubject();
                Token token = new Token();
                token.setAccessToken(createJWT(username,MILLIS_MINUTE * expireTime));
                token.setRefreshToken(createJWT(username,MILLIS_MINUTE * expireTime1));
                return token;
            }catch (Exception e){
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
        }
        return null;
    }

    public LoginUser getLoginUser(HttpServletRequest request){
        String token = getAccessToken(request);
        if (StringUtils.isNotEmpty(token)){
            Claims claims;
            try{
                claims = parseJWT(token);
                //提取出jwt中的username
                String username = claims.getSubject();
                LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);
                return loginUser;
            }catch (Exception e){
                throw new BusinessException(ErrorCode.JWT_EXPIRED_ERROR);
            }
        }
        return null;
    }

    /**
     * 从请求头获取access_token
     * @param request
     * @return
     */
    private String getAccessToken(HttpServletRequest request){
        return request.getHeader(header);
    }

    /**
     * 从请求头获取refresh_token
     * @param request
     * @return
     */
    private String getRefreshToken(HttpServletRequest request){
        return request.getHeader(header1);
    }


    /**
     * 生成jtw，默认有效期JWT_TTL 3小时
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, UUID.fastUUID().toString());// 设置过期时间
        return builder.compact();
    }
    /**
     * 生成jtw，并自定义token过期时间
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, UUID.fastUUID().toString());// 设置过期时间
        return builder.compact();
    }

    private JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //如果没有设置过期时间。则默认20分钟过期
        if(ttlMillis==null){
            ttlMillis= MILLIS_MINUTE_TEN;
        }
        //过期时间
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("js")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate); //过期时间
    }

    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 解析jwt，返回
     * @param jwt
     * @return claims
     * @throws Exception
     */
    public Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

}
