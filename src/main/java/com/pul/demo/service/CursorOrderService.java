package com.pul.demo.service;

import com.pul.demo.exception.FF14Exception;
import com.pul.demo.po.CursorOrderPO;
import com.pul.demo.repo.CursorOrderRepo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author chentong
 * @version 1.0
 * @description: Cursor订单服务
 * @date 2025/2/12 16:58
 */
@Slf4j
@Service
public class CursorOrderService {

	@Resource
	private CursorOrderRepo cursorOrderRepo;

	@Value("${cursor.expire-days:30}")
	private Integer expireDays;

	public Long createOrder(String orderId, String userName) {
		// 检查订单是否已存在
		if (cursorOrderRepo.findByOrderId(orderId).isPresent()) {
			throw new FF14Exception("订单已存在");
		}

		LocalDateTime now = LocalDateTime.now();

		CursorOrderPO order = new CursorOrderPO();
		order.setOrderId(orderId);
		order.setCreateTime(now);
		order.setUserName(userName);
		order.setExpireTime(now.plusDays(expireDays));

		return cursorOrderRepo.save(order).getId();
	}

	/**
	 * 获取Windows PowerShell脚本
	 * 返回纯文本格式，支持 irm | iex 直接执行
	 */
	public ResponseEntity<String> getWindowsScript(String orderId) {
		CursorOrderPO order = findById(orderId);

		// 检查订单是否过期
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(order.getExpireTime())) {
			throw new FF14Exception("订单已超过30天，请重新下单");
		}

		try {
			// 读取脚本文件
			ClassPathResource resource = new ClassPathResource("cursor_script/cursor_windows.ps1");
			
			// 使用UTF-8编码读取文件
			byte[] scriptBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
			String script = new String(scriptBytes, StandardCharsets.UTF_8);

			// 设置响应头，确保正确的编码
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("text/plain; charset=UTF-8"));
			
			return ResponseEntity.ok()
					.headers(headers)
					.body(script);

		} catch (IOException e) {
			log.error("读取脚本文件失败", e);
			throw new FF14Exception("获取脚本文件失败");
		}
	}

	/**
	 * 获取Linux Shell脚本
	 */
	public ResponseEntity<String> getLinuxScript(String orderId) {
		CursorOrderPO order = findById(orderId);

		// 检查订单是否过期
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(order.getExpireTime())) {
			throw new FF14Exception("订单已超过30天，请重新下单");
		}

		try {
			// 读取脚本文件
			ClassPathResource resource = new ClassPathResource("cursor_script/cursor_linux_id_modifier.sh");
			
			// 使用UTF-8编码读取文件
			byte[] scriptBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
			String script = new String(scriptBytes, StandardCharsets.UTF_8);

			// 设置响应头，确保正确的编码
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("text/plain; charset=UTF-8"));
			
			return ResponseEntity.ok()
					.headers(headers)
					.body(script);

		} catch (IOException e) {
			log.error("读取脚本文件失败", e);
			throw new FF14Exception("获取脚本文件失败");
		}
	}

	/**
	 * 获取Mac Shell脚本
	 */
	public ResponseEntity<String> getMacScript(String orderId) {
		CursorOrderPO order = findById(orderId);

		// 检查订单是否过期
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(order.getExpireTime())) {
			throw new FF14Exception("订单已超过30天，请重新下单");
		}

		try {
			// 读取脚本文件
			ClassPathResource resource = new ClassPathResource("cursor_script/cursor_mac_id_modifier.sh");
			
			// 使用UTF-8编码读取文件
			byte[] scriptBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
			String script = new String(scriptBytes, StandardCharsets.UTF_8);

			// 设置响应头，确保正确的编码
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType("text/plain; charset=UTF-8"));
			
			return ResponseEntity.ok()
					.headers(headers)
					.body(script);

		} catch (IOException e) {
			log.error("读取脚本文件失败", e);
			throw new FF14Exception("获取脚本文件失败");
		}
	}

	public CursorOrderPO findById(String orderId) {
		return cursorOrderRepo.findByOrderId(orderId)
				.orElseThrow(() -> new FF14Exception("订单不存在"));
	}

}
