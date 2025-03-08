package com.pul.demo.exception;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 12:04
 */
@Getter
public class FF14Exception extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1405961065280191434L;

	public static final int DEFAULT_CODE = 500;
	public static final String DEFAULT_MSG = "系统错误";

	private final int code;

	@Setter(AccessLevel.PROTECTED)
	private boolean log;

	private final String message;

	protected FF14Exception(int code, String message, Throwable cause, boolean log) {
		super(cause);
		this.code = code;
		this.message = message;
		this.log = log;
	}


	protected FF14Exception(int code, String message, Throwable cause) {
		this(code, message, cause, false);
	}

	protected FF14Exception(int code, String message) {
		this(code, message, null, false);
	}


	public FF14Exception() {
		this(DEFAULT_CODE, DEFAULT_MSG, null, false);
	}

	public FF14Exception(String message) {
		this(DEFAULT_CODE, message, null, false);
	}

	public FF14Exception(String message, Throwable cause, boolean log) {
		this(DEFAULT_CODE, message, cause, log);
	}

	public FF14Exception(String message, Throwable cause) {
		this(DEFAULT_CODE, message, cause, false);
	}


	public static class WorldException extends RuntimeException {

		public WorldException(String message) {
			super(message);
		}

		public static WorldException notFound(String worldName) {
			return new WorldException(CharSequenceUtil.format("世界 {} 不存在", worldName));
		}

		public static WorldException notFound() {
			return new WorldException("世界不存在");
		}
	}

	public static class UserException extends FF14Exception {

		public static UserException notFound(String account) {
			return new UserException("用户不存在：" + account);
		}

		public static UserException notFound() {
			return new UserException("用户不存在");
		}

		public static UserException passwordError() {
			return new UserException("密码错误");
		}

		public static UserException emailExists(String email) {
			return new UserException("邮箱已存在：" + email);
		}

		public static UserException emailBoundToAnotherUser() {
			return new UserException("邮箱已被其他用户绑定");
		}

		private UserException(String message) {
			super(message);
		}
	}

	public static class LoginException extends FF14Exception {
		public static LoginException loginInvalid() {
			return new LoginException("登录失败");
		}

		private LoginException(String message) {
			super(401, message);
		}
	}

}
