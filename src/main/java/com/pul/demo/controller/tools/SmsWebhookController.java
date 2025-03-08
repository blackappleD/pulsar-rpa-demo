package com.pul.demo.controller.tools;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/sms_hook")
public class SmsWebhookController {

	// enterprise_phone
	private static final String CAPTCHA_KET_TEMPLATE = "{}_{}";

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Data
	public static class CaptchaSms {
		// SIM1_18349342711___【京东】验证码：117384，您正在新设备上登录。请确认本人操作，切勿泄露给他人，京东工作人员不会索取此验证码。___2025-03-08 12:45:07
		private String message;
	}

	@PostMapping("/captcha")
	public void smsCaptchaReceive(@RequestParam String enterprise,
	                              @RequestBody CaptchaSms params) {
		String message = params.getMessage();
		log.info(CharSequenceUtil.format("接收到 {} 验证码，内容: {} ", enterprise, message));
		String[] split = message.split("___");

		String phone;
		String captCha;
		String[] phoneSplit = split[0].split("_");
		if (phoneSplit.length < 2) {
			phone = phoneSplit[0];
		} else {
			phone = phoneSplit[1];
		}

		// 定义正则表达式，匹配"验证码："后的数字
		String regex = "验证码[：:](\\d+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(split[1]);

		if (matcher.find()) {
			captCha = matcher.group(1);
		} else {
			captCha = "";
		}
		String redisKey = CharSequenceUtil.format(CAPTCHA_KET_TEMPLATE, enterprise, phone);

		// 将验证码写入Redis，设置过期时间
		if (CharSequenceUtil.isNotBlank(captCha)) {
			stringRedisTemplate.opsForValue().set(redisKey, captCha, 5, TimeUnit.MINUTES);
			log.info("验证码已写入Redis，key: {}，验证码: {}", redisKey, captCha);
		} else {
			log.warn("未能从短信中提取到验证码，消息内容: {}", message);
		}

	}

}