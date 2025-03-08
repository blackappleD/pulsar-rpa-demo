package com.pul.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/26 16:11
 */
@Data
public class LongIdDTO {

	@Schema(description = "主键id")
	private Long id;

}
