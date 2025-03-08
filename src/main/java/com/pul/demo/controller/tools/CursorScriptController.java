package com.pul.demo.controller.tools;

import com.pul.demo.service.CursorOrderService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cursor")
public class CursorScriptController {

	@Resource
	private CursorOrderService cursorOrderService;

	@GetMapping("/script_windows")
	public ResponseEntity<String> searchItems(@RequestParam String orderId) {

		return cursorOrderService.getWindowsScript(orderId);

	}

	@PostMapping("/order")
	public Long createOrder(@RequestParam String orderId,
	                        @RequestParam String userName) {

		return cursorOrderService.createOrder(orderId, userName);

	}

	@GetMapping("/script_linux")
	public ResponseEntity<String> getLinuxScript(@RequestParam String orderId) {
		return cursorOrderService.getLinuxScript(orderId);
	}

	@GetMapping("/script_mac")
	public ResponseEntity<String> getMacScript(@RequestParam String orderId) {
		return cursorOrderService.getMacScript(orderId);
	}

}