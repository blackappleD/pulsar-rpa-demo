package com.pul.demo.util;

import cn.hutool.extra.spring.SpringUtil;
import com.pul.demo.po.FF14UserPO;
import com.pul.demo.service.FF14UserService;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/27 15:23
 */
public class AdminUtil {

	public static FF14UserPO getCurrentUser() {

		return SpringUtil.getBean(FF14UserService.class).findById(AuthUtil.getLoginId());
	}
}
