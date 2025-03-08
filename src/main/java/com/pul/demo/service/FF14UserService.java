package com.pul.demo.service;

import cn.hutool.crypto.digest.BCrypt;
import com.pul.demo.auth.TokenInfo;
import com.pul.demo.dto.UserRegisterReqDTO;
import com.pul.demo.dto.UserResDTO;
import com.pul.demo.dto.LoginReqDTO;
import com.pul.demo.exception.FF14Exception;
import com.pul.demo.mapper.FF14UserMapper;
import com.pul.demo.po.FF14UserPO;
import com.pul.demo.repo.FF14UserRepo;
import com.pul.demo.util.AuthUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/20 9:27
 */
@Service
public class FF14UserService {

	@Resource
	private FF14UserRepo ff14UserRepo;

	@Resource
	private FF14UserMapper ff14UserMapper;

	@Resource
	private FF14SubscribeCfgService ff14SubscribeCfgService;

	public UserResDTO get(String id) {
		return ff14UserMapper.po2dto(findById(id));
	}

	public FF14UserPO findById(String id) {
		Optional<FF14UserPO> byId = ff14UserRepo.findById(id);
		if (byId.isEmpty()) {
			throw FF14Exception.UserException.notFound();
		}
		return byId.get();
	}

	public TokenInfo login(LoginReqDTO dto) {
		Optional<FF14UserPO> userOpt = ff14UserRepo.findByUserAccount(dto.getUserAccount());
		if (userOpt.isEmpty()) {
			userOpt = ff14UserRepo.findByEmail(dto.getUserAccount());
		}

		if (userOpt.isEmpty()) {
			throw FF14Exception.UserException.notFound(dto.getUserAccount());
		}

		FF14UserPO user = userOpt.get();

		if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
			throw FF14Exception.UserException.passwordError();
		}
		user.setLastLoginTime(LocalDateTime.now());
		ff14UserRepo.save(user);
		return AuthUtil.login(user.getId());
	}

	public void logout() {
		AuthUtil.logout();
	}

	@Transactional
	public void insert(UserRegisterReqDTO dto) {

		if (ff14UserRepo.existsByEmail(dto.getEmail())) {
			throw FF14Exception.UserException.emailExists(dto.getEmail());
		}
		FF14UserPO user = ff14UserMapper.dto2po(dto);
		// 加密密码
		user.setPassword(BCrypt.hashpw(dto.getPassword()));
		ff14UserRepo.save(user);
		user.setRegisterTime(LocalDateTime.now());
		ff14SubscribeCfgService.create(user);

	}

	public void changeEmail(UserRegisterReqDTO dto) {

		FF14UserPO user = findById(AuthUtil.getLoginId());
		if (user.getEmail().equals(dto.getEmail())) {
			return;
		}
		if (ff14UserRepo.existsByEmail(dto.getEmail())) {
			throw FF14Exception.UserException.emailBoundToAnotherUser();
		}
		user.setEmail(dto.getEmail());
		ff14UserRepo.save(user);
	}

	public List<FF14UserPO> findAllUser() {
		return ff14UserRepo.findAll();
	}

}
