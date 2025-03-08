package com.pul.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/26 15:54
 */
@Data
public class ItemSubReqDTO {

	@NotNull(message = "物品不能为空")
	@Schema(description = "物品信息")
	private LongIdDTO item;

	@Schema(description = "是否只订阅hq")
	private Boolean hq = false;

	@Schema(description = "价格阈值")
	private Long notifyThreshold;
}
