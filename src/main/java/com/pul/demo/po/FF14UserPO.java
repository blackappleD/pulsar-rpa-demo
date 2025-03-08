package com.pul.demo.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 10:51
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = FF14UserPO.TABLE_NAME)
public class FF14UserPO {

	public static final String TABLE_NAME = "ff14_user";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountIdGenerator")
	@GenericGenerator(name = "accountIdGenerator", strategy = "com.pul.demo.po.generator.AccountIdGenerator")
	private String id;

	@Column(nullable = false, length = 30, unique = true)
	@Comment("账户名")
	private String userAccount;

	@Column(nullable = false, length = 30)
	@Comment("用户名称")
	private String userName;

	@Column(nullable = false, length = 60, unique = true)
	@Comment("邮箱")
	private String email;

	@Column(length = 60)
	@Comment("密码")
	private String password;

	@Comment("最后登录时间")
	private LocalDateTime lastLoginTime;

	@Comment("注册时间")
	private LocalDateTime registerTime;

}
