package com.pul.demo.controller;

import com.pul.demo.annotations.FF14ResponseBody;
import com.pul.demo.dto.ItemPriceInfo;
import com.pul.demo.dto.SubscribePriceGroup;
import com.pul.demo.service.FF14PriceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:13
 */
@RestController
@RequestMapping("/ff14/price")
@FF14ResponseBody
public class FF14PriceController {

	@Resource
	private FF14PriceService ff14PriceService;

	@GetMapping("/on_time")
	public List<SubscribePriceGroup> subscribeItemPriceOnTime() {
		return ff14PriceService.subscribeItemPriceOnTime();
	}

	@GetMapping("/on_time/{worldName}/{itemId}")
	public List<ItemPriceInfo> requestItemPriceInfo(@PathVariable String worldName,
			@PathVariable Integer itemId,
			@RequestParam(defaultValue = "false") Boolean hq) {
		return ff14PriceService.requestItemPriceInfo(worldName, itemId, hq);
	}

}
