package com.pul.demo.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 9:48
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = FF14ItemPO.TABLE_NAME)
public class FF14ItemPO {


	public static final String TABLE_NAME = "ff14_item";

	@Id
	private Long id;


	private String name;

}
