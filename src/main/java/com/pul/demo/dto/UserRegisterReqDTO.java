package com.pul.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/20 9:28
 */
@Data
public class UserRegisterReqDTO {

	@NotBlank(message = "userName不能为空")
	private String userName;

	@NotBlank(message = "密码不能为空")
	private String password;

	@NotBlank(message = "账户名不能为空")
	private String userAccount;

	@NotBlank(message = "邮箱不能为空")
	private String email;
}
