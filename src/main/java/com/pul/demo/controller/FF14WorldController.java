package com.pul.demo.controller;

import com.pul.demo.annotations.FF14ResponseBody;
import com.pul.demo.dto.WorldDTO;
import com.pul.demo.service.FF14WorldService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ff14/world")
@FF14ResponseBody
public class FF14WorldController {

	@Resource
	private FF14WorldService ff14WorldService;

	@GetMapping("/search")
	public List<WorldDTO> searchWorlds(@RequestParam String name) {
		return ff14WorldService.searchWorlds(name);
	}
}