package com.pul.demo.service;

import com.pul.demo.dto.ItemDTO;
import com.pul.demo.exception.FF14Exception;
import com.pul.demo.mapper.FF14ItemMapper;
import com.pul.demo.po.FF14ItemPO;
import com.pul.demo.repo.FF14ItemRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:58
 */
@Service
public class FF14ItemService {

	@Resource
	private FF14ItemRepo ff14ItemRepo;

	@Resource
	private FF14ItemMapper ff14ItemMapper;

	public List<ItemDTO> searchItems(String name) {
		List<FF14ItemPO> items = ff14ItemRepo.findByNameContaining(name);
		return items.stream()
				.map(ff14ItemMapper::po2dto)
				.collect(Collectors.toList());
	}

	public FF14ItemPO findById(Long id) {
		return ff14ItemRepo.findById(id).orElseThrow(() -> new FF14Exception("物品不存在"));
	}

}
