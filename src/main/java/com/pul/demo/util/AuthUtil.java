package com.pul.demo.util;

import cn.hutool.extra.spring.SpringUtil;
import com.pul.demo.auth.CustomLogic;
import com.pul.demo.auth.TokenInfo;
import com.pul.demo.auth.UserEntity;
import com.pul.demo.exception.FF14Exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.util.Comparator;
import java.util.Set;

public class AuthUtil {
	private AuthUtil() {

	}

	private static final ThreadLocal<HttpServletRequest> requestLocal = new ThreadLocal<>();
	private static final ThreadLocal<HttpServletResponse> responseLocal = new ThreadLocal<>();
	private static final ThreadLocal<TokenInfo> tokenLocal = new ThreadLocal<>();

	private volatile static CustomLogic logic;

	public static CustomLogic getLogic() {
		if (logic == null) {
			synchronized (AuthUtil.class) {
				if (logic == null) {
					logic = SpringUtil.getBeansOfType(CustomLogic.class).values()
							.stream()
							.max(Comparator.comparingInt(CustomLogic::getOrder))
							.orElse(null);
				}
			}
		}
		return logic;
	}

	public static String getLoginId() {
		var token = getMyToken();
		if (token == null) {
			throw FF14Exception.LoginException.loginInvalid();
		}
		return token.getUserId();
	}

	public static String getLoginIdDefault(String defaultValue) {
		var token = getMyToken();
		if (token == null) {
			return defaultValue;
		}
		return token.getUserId();
	}

	public static TokenInfo login(UserEntity user) {
		var tokenInfo = getLogic().login(user);
		tokenLocal.set(tokenInfo);
		return tokenInfo;
	}

	public static TokenInfo login(String loginId) {
		var user = new UserEntity();
		user.setId(loginId);
		return login(user);
	}

	public static TokenInfo getMyToken() {
		var tokenInfo = tokenLocal.get();
		if (tokenInfo == null) {
			tokenInfo = getLogic().getTokenInfo();
			if (tokenInfo != null) {
				tokenLocal.set(tokenInfo);
			}
		}
		return tokenInfo;
	}

	public static void setRequest(HttpServletRequest request) {
		requestLocal.set(request);
	}

	public static HttpServletRequest getRequest() {
		return requestLocal.get();
	}

	public static void setResponse(HttpServletResponse response) {
		responseLocal.set(response);
	}

	public static TokenInfo getToken() {
		return tokenLocal.get();
	}

	public static void setToken(TokenInfo token) {
		tokenLocal.set(token);
	}

	public static void removeToken() {
		tokenLocal.remove();
	}

	/**
	 * 清除线程缓存，每次请求完成后都需要调用一次
	 */
	public static void clearThreadCache() {
		tokenLocal.remove();
		requestLocal.remove();
		responseLocal.remove();
	}

	public static HttpServletResponse getResponse() {
		return responseLocal.get();
	}

	public static boolean isLogin() {
		return getMyToken() != null;
	}

	public static Set<String> getRoles() {
		return getLogic().getRoles(getLoginId());
	}

	public static Set<String> getRequestPerms(HttpServletRequest request, HandlerMethod handler) {
		return getLogic().getRequestPerms(request, handler);
	}

	public static Set<String> getUserPerms() {
		return getLogic().getUserPerms(getLoginId());
	}

	public static boolean checkUrlPerms(Set<String> urlPerms, Set<String> userPerms) {
		return getLogic().checkUrlPerms(urlPerms, userPerms);
	}

	public static void logout() {
		getLogic().logout();
		AuthUtil.removeToken();
	}
}
