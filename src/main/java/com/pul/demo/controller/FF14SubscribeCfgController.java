package com.pul.demo.controller;

import com.pul.demo.annotations.FF14ResponseBody;
import com.pul.demo.dto.SubscribeCfgResDTO;
import com.pul.demo.service.FF14SubscribeCfgService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:13
 */
@RestController
@RequestMapping("/ff14/sub_cfg")
@FF14ResponseBody
public class FF14SubscribeCfgController {

	@Resource
	private FF14SubscribeCfgService ff14SubscribeCfgService;

	@PutMapping("/notify")
	public void modifyNotify(@RequestParam Boolean notify) {
		ff14SubscribeCfgService.modifyNotify(notify);
	}

	@GetMapping
	public SubscribeCfgResDTO get() {
		return ff14SubscribeCfgService.get();
	}

}
