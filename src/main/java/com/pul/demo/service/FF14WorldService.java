package com.pul.demo.service;

import com.pul.demo.dto.WorldDTO;
import com.pul.demo.exception.FF14Exception;
import com.pul.demo.mapper.FF14WorldMapper;
import com.pul.demo.po.FF14WorldPO;
import com.pul.demo.repo.FF14WorldRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/20 10:53
 */
@Service
public class FF14WorldService {

	@Resource
	private FF14WorldRepo ff14WorldRepo;

	@Resource
	private FF14WorldMapper ff14WorldMapper;

	public List<WorldDTO> searchWorlds(String name) {
		List<FF14WorldPO> worlds = ff14WorldRepo.findByNameContaining(name);
		return worlds.stream()
				.map(ff14WorldMapper::po2dto)
				.collect(Collectors.toList());
	}

	public FF14WorldPO findById(Long id) {
		return ff14WorldRepo.findById(id).orElseThrow(FF14Exception.WorldException::notFound);
	}

	public FF14WorldPO findByName(String name) {
		Optional<FF14WorldPO> byName = ff14WorldRepo.findByName(name);
		if (byName.isEmpty()) {
			throw FF14Exception.WorldException.notFound(name);
		}
		return byName.get();

	}

}
