package com.pul.demo.po;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:48
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = CursorOrderPO.TABLE_NAME)
public class CursorOrderPO {

	public static final String TABLE_NAME = "cursor_order";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId;

	private String userName;

	private LocalDateTime createTime;

	private LocalDateTime expireTime;

}
