package com.pul.demo.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.pul.demo.config.CacheLogoutConfig;
import com.pul.demo.config.JwtConfig;
import com.pul.demo.util.AuthUtil;
import com.pul.demo.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

/**
 * 默认的jwt登录逻辑
 *
 * @author kong
 */
public class DefaultJwtLogic implements CustomLogic {
    private static final String LOGOUT2 = "logout";
    private final String secret;
    private final String prefix;
    private final String loginIdAlias;
    private final long expire;
    private final Boolean logout;

    @Override
    public int getOrder() {
        return 10;
    }

    /**
     * Sa-Token 整合 jwt -- stateless 无状态
     */
    public DefaultJwtLogic(JwtConfig jwtConfig) {
        this.secret = jwtConfig.getSecret();
        this.prefix = jwtConfig.getPrefix();
        this.expire = jwtConfig.getExpire();
        this.loginIdAlias = jwtConfig.getLoginIdAlias();
        this.logout = jwtConfig.getLogout();

    }

    /**
     * 登录
     */
    public TokenInfo login(UserEntity user) {
        return login(user, expire);
    }

    public TokenInfo login(UserEntity user, long expire) {
        var tokenValue = CharSequenceUtil.isEmpty(loginIdAlias) ? JwtUtils.sign(user.getId(), expire, secret)
                : JwtUtils.sign(loginIdAlias, user.getId(), expire, secret);
        TokenInfo tokenInfo = getTokenInfoByTokenValue(tokenValue);
        AuthUtil.setToken(tokenInfo);
        return tokenInfo;
    }

    public String getTokenValue() {
        var request = AuthUtil.getRequest();
        if (request == null) {
            return null;
        }
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && prefix != null && token.startsWith(prefix)) {
            String tokenValue = token.substring(prefix.length());
            if (Boolean.TRUE.equals(logout)) {
                CacheManager bean = SpringUtil.getBean(CacheLogoutConfig.LOGOUT_CACHE_MANAGER, CacheManager.class);
                Cache cache = bean.getCache(LOGOUT2);
                if (cache != null) {
                    String value = cache.get(tokenValue, String.class);
                    if (LOGOUT2.equals(value)) {
                        return null;
                    }
                }
            }
            return tokenValue;
        }
        return token;
    }

    /**
     * 会话注销
     */
    public void logout() {
        if (Boolean.TRUE.equals(logout)) {
            CacheManager bean = SpringUtil.getBean(CacheLogoutConfig.LOGOUT_CACHE_MANAGER, CacheManager.class);
            Cache cache = bean.getCache(LOGOUT2);
            if (cache != null) {
                cache.put(AuthUtil.getMyToken().getTokenValue(), LOGOUT2);
            }
        }
    }

    public TokenInfo getTokenInfo() {
        var tokenValue = getTokenValue();
        if (tokenValue == null) {
            return null;
        }
        return getTokenInfoByTokenValue(tokenValue);
    }

    private TokenInfo getTokenInfoByTokenValue(String tokenValue) {
        if (JwtUtils.verify(tokenValue, secret)) {
            var jwt = JwtUtils.getPayload(tokenValue);
            var tokenInfo = new TokenInfo();
            tokenInfo.setTokenValue(tokenValue);
            if (!CharSequenceUtil.isEmpty(loginIdAlias)) {
                tokenInfo.setUserId(jwt.getClaim(loginIdAlias).as(Object.class).toString());
            } else {
                tokenInfo.setUserId(jwt.getClaim(JwtUtils.LOGIN_ID).asString());
            }
            tokenInfo.setExpireAt(LocalDateTime.ofInstant(jwt.getExpiresAt().toInstant(), ZoneId.systemDefault()));
            return tokenInfo;
        } else {
            return null;
        }
    }

    @Override
    public Set<String> getRoles(String loginId) {
        return CollUtil.newHashSet();
    }

    @Override
    public Set<String> getRequestPerms(HttpServletRequest request, HandlerMethod handler) {
        return CollUtil.newHashSet();
    }

    @Override
    public Set<String> getUserPerms(String loginId) {
        return CollUtil.newHashSet();
    }

    @Override
    public boolean checkUrlPerms(Set<String> urlPerms, Set<String> userPerms) {
        for (var p : urlPerms) {
            if (!userPerms.contains(p)) {
                return false;
            }
        }
        return true;
    }
}
