package com.pul.demo.po;

import com.pul.demo.enums.WorldLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 10:51
 */
@Getter
@Setter
@FieldNameConstants
@Entity(name = FF14WorldPO.TABLE_NAME)
public class FF14WorldPO {

	public static final String TABLE_NAME = "ff14_world";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long worldId;

	@Enumerated(EnumType.STRING)
	private WorldLevel level;

	@Column(nullable = false, length = 30)
	private String name;

}
