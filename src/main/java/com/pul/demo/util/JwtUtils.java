package com.pul.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

public class JwtUtils {

    /**
     * key：账号类型
     */
    public static final String LOGIN_TYPE = "loginType";

    /**
     * key：账号id
     */
    public static final String LOGIN_ID = "loginId";

    /**
     * key：登录设备
     */
    public static final String DEVICE = "device";

    /**
     * key：有效截止期 (时间戳)
     */
    public static final String EFF = "eff";

    private JwtUtils() {
    }

    /**
     * 验证token是否正确
     */
    public static boolean verify(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    /**
     * 获得token中的自定义信息，无需secret解密也能获得
     */
    public static String getClaimFiled(String token, String filed) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(filed).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static DecodedJWT getPayload(String token) {
        return JWT.decode(token);
    }

    /**
     * 生成签名
     */
    public static String sign(Object loginId, long timeout, String secret) {
        return sign(LOGIN_ID, loginId, timeout, secret);
    }

    /**
     * 生成签名
     */
    public static String sign(String loginIdKey, Object loginId, long timeout, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + timeout);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带username，nickname信息
            return JWT.create()
                    .withClaim(loginIdKey, loginId.toString())
                    .withExpiresAt(date).sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    /**
     * 获取 token的签发时间
     */
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 验证 token是否过期
     */
    public static boolean isTokenExpired(String token) {
        Date now = Calendar.getInstance().getTime();
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(now);
    }

    /**
     * 刷新 token的过期时间
     */
    public static String refreshTokenExpired(String token, String secret, long timeout) {
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        try {
            Builder builer = JWT.create();
            for (Entry<String, Claim> entry : claims.entrySet()) {
                builer.withClaim(entry.getKey(), entry.getValue().asString());
            }
            Date date = new Date(System.currentTimeMillis() + timeout);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            builer = builer.withExpiresAt(date);
            return builer.sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    public static Long getTimeout(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().getTime() - new Date().getTime();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static Object getLoginId(String token, String secret) {
        return getLoginId(LOGIN_ID, token, secret);
    }

    public static Object getLoginId(String loginIdKey, String token, String secret) {
        try {
            JwtUtils.verify(token, secret);
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(loginIdKey).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}