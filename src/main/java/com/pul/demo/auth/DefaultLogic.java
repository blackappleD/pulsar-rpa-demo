package com.pul.demo.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.pul.demo.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;

import java.util.Set;

/**
 * 默认的登录逻辑
 *
 * @author kong
 */
public class DefaultLogic implements CustomLogic {

	@Override
	public int getOrder() {
		return 1;
	}

	/**
	 * 登录
	 */
	public TokenInfo login(UserEntity user) {
		return login(user, -1);
	}

	public TokenInfo login(UserEntity user, long expire) {
		var tokenInfo = new TokenInfo();
		tokenInfo.setExpireAt(null);
		tokenInfo.setTokenValue(user.getId());
		tokenInfo.setUserId(user.getId());
		AuthUtil.setToken(tokenInfo);
		return tokenInfo;
	}

	/**
	 * 会话注销
	 */
	public void logout() {
		AuthUtil.removeToken();
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
	public TokenInfo getTokenInfo() {
		var request = AuthUtil.getRequest();
		if (request == null) {
			return null;
		}
		var token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (CharSequenceUtil.isEmpty(token)) {
			return null;
		}
		var tokenInfo = new TokenInfo();
		tokenInfo.setExpireAt(null);
		tokenInfo.setTokenValue(token);
		if (token.startsWith("Bearer ")) {
			tokenInfo.setUserId(token.substring(6).strip());
		} else {
			tokenInfo.setUserId(token);
		}
		return tokenInfo;
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
