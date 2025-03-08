package com.pul.demo.exception;

/**
 * Json字符串处理失败
 */
public class JsonException extends RuntimeException {
	public JsonException(Throwable t) {
		this(t, false);
	}

	public JsonException(Throwable t, boolean deserialization) {
		super(deserialization ? "json反序列化错误：" + t.getMessage() : "json序列化错误: " + t.getMessage());
	}
}
