package com.pul.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/17 14:35
 */
@Data
public class ItemPriceInfo {
	private long lastReviewTime;

	@Schema(description = "物品单价")
	private long pricePerUnit;
	private int quantity;
	private int stainId;

	@Schema(description = "所在区服")
	private String worldName;
	
	private int worldId;
	@Schema(description = "生产者名称")
	private String creatorName;
	private String creatorId;

	@Schema(description = "品质")
	private boolean hq;
	private boolean isCrafted;
	private String listingId;
	private boolean onMannequin;
	private int retainerCity;
	private String retainerId;

	@Schema(description = "雇员名称")
	private String retainerName;
	private String sellerId;

	@Schema(description = "总价")
	private long total;

	@Schema(description = "手续费")
	private int tax;

	@Schema(description = "是否低于阈值")
	private boolean lowerThreshold;
}

