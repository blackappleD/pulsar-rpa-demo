package com.pul.demo.service;

import com.pul.demo.dto.SubscribeCfgReqDTO;
import com.pul.demo.dto.SubscribeCfgResDTO;
import com.pul.demo.exception.FF14Exception;
import com.pul.demo.mapper.FF14SubscribeCfgMapper;
import com.pul.demo.po.FF14SubscribeCfgPO;
import com.pul.demo.po.FF14UserPO;
import com.pul.demo.repo.FF14SubscribeCfgRepo;
import com.pul.demo.util.AdminUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2025/1/20 9:00
 */
@Service
public class FF14SubscribeCfgService {

	@Resource
	private FF14SubscribeCfgRepo ff14SubscribeCfgRepo;

	@Resource
	private FF14SubscribeCfgMapper ff14SubscribeCfgMapper;

	public void modifyNotify(Boolean notify) {

		Optional<FF14SubscribeCfgPO> byUser = ff14SubscribeCfgRepo.findByUser(AdminUtil.getCurrentUser());
		if (byUser.isPresent()) {
			FF14SubscribeCfgPO ff14SubscribeCfg = byUser.get();
			ff14SubscribeCfg.setNotify(notify);

			ff14SubscribeCfgRepo.save(ff14SubscribeCfg);
		}
	}

	public SubscribeCfgResDTO get() {

		FF14SubscribeCfgPO byUser = findByUser(AdminUtil.getCurrentUser());
		return ff14SubscribeCfgMapper.po2dto(byUser);
	}

	public FF14SubscribeCfgPO findByUser(FF14UserPO user) {

		return ff14SubscribeCfgRepo.findByUser(user).orElseThrow(() -> new FF14Exception("订阅配置不存在"));
	}

	public void create(FF14UserPO user) {

		FF14SubscribeCfgPO po = new FF14SubscribeCfgPO();
		po.setUser(user);
		ff14SubscribeCfgRepo.save(po);

	}

	public void update(SubscribeCfgReqDTO dto) {
		FF14SubscribeCfgPO subscribeCfg = findById(dto.getId());
		ff14SubscribeCfgMapper.dto2po(dto, subscribeCfg);
		ff14SubscribeCfgRepo.save(subscribeCfg);
	}

	public FF14SubscribeCfgPO findById(Long id) {
		return ff14SubscribeCfgRepo.findById(id).orElseThrow(() -> new FF14Exception("订阅配置不存在"));
	}

}
