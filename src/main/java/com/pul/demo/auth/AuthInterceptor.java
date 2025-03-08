package com.pul.demo.auth;

import com.pul.demo.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		AuthUtil.clearThreadCache();

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return true;
		}

		if (!(handler instanceof HandlerMethod)) {
			return false;
		}

		AuthUtil.setRequest(request);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		try {
			HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
		} finally {
			AuthUtil.clearThreadCache();
		}
	}
}
