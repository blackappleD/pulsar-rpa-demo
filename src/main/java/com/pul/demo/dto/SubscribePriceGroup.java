package com.pul.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/27 17:29
 */
@Data
public class SubscribePriceGroup {

	@Schema(description = "订阅分组id")
	private Long id;

	@Schema(description = "订阅组区服名")
	private String worldName;

	@Schema(description = "物价信息")
	private List<ItemPriceGroup> itemPriceGroups;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemPriceGroup {

		@Schema(description = "物品id")
		private Long id;

		@Schema(description = "物品名称")
		private String name;

		@Schema(description = "物品价格信息")
		private List<ItemPriceInfo> itemPriceInfoList;



	}

}
