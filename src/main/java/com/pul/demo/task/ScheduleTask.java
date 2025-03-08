package com.pul.demo.task;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.pul.demo.dto.SubscribePriceGroup;
import com.pul.demo.po.FF14SubscribeCfgPO;
import com.pul.demo.service.FF14MailService;
import com.pul.demo.service.FF14PriceService;
import com.pul.demo.service.FF14SubscribeCfgService;
import com.pul.demo.service.FF14UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/17 12:59
 */
@Slf4j
@Component
public class ScheduleTask {

	@Resource
	private FF14SubscribeCfgService ff14SubscribeCfgService;

	@Resource
	private FF14MailService ff14MailService;

	@Resource
	private FF14PriceService ff14PriceService;

	@Resource
	private FF14UserService ff14UserService;

	@Scheduled(cron = "0 0/30 * * * ? ")
	public void ff14Task() {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime beginOfDay = LocalDateTimeUtil.beginOfDay(now);
		if (now.isAfter(beginOfDay.plusHours(23)) || now.isBefore(beginOfDay.plusHours(8))) {
			log.info("当前时间：{}，处于免打扰时间段，不执行推送任务！", now);
			return;
		}
		ff14UserService.findAllUser().forEach(user -> {
			FF14SubscribeCfgPO subCfg = ff14SubscribeCfgService.findByUser(user);
			if (subCfg.getNotify()) {
				List<SubscribePriceGroup> subscribePriceGroups = ff14PriceService.subscribeItemPrice(user);
				ff14MailService.sendPriceSubscriptions(subscribePriceGroups, user.getEmail());
			}
		});

	}

}
