package com.pul.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2025/1/13 13:48
 */
@Configuration
public class ThreadPoolExecutorConfig {

	@Bean("uniHttpReqExecutor")
	public ThreadPoolExecutor threadPoolExecutor() {
		return new ThreadPoolExecutor(4, 4,
				1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new Sleep5sResubmitHandler());
	}

	public static class Sleep5sResubmitHandler implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			try {
				Thread.sleep(5000);
				executor.submit(r);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
