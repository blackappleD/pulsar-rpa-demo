package com.pul.demo.controller;

import com.pul.demo.annotations.FF14ResponseBody;
import com.pul.demo.auth.TokenInfo;
import com.pul.demo.dto.UserRegisterReqDTO;
import com.pul.demo.dto.UserResDTO;
import com.pul.demo.dto.LoginReqDTO;
import com.pul.demo.service.FF14UserService;
import com.pul.demo.util.AuthUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/20 9:26
 */
@RestController
@RequestMapping("/ff14/user")
@FF14ResponseBody
public class FF14UserController {

	@Resource
	private FF14UserService ff14UserService;

	@PostMapping("/register")
	public void add(@RequestBody @Valid UserRegisterReqDTO dto) {
		ff14UserService.insert(dto);
	}

	@GetMapping("/info")
	public UserResDTO userInfo() {
		return ff14UserService.get(AuthUtil.getLoginId());
	}

	@PostMapping("/login")
	public TokenInfo login(@RequestBody @Valid LoginReqDTO dto) {
		return ff14UserService.login(dto);
	}

	@PostMapping("/logout")
	public void logout() {
		ff14UserService.logout();
	}

}
