package com.pul.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 12:00
 */
@Data
public class SubscribeGroupResDTO {

	@Schema(description = "id")
	private Long id;

	@Schema(description = "区服")
	private WorldDTO world;

	@Schema(description = "物品列表")
	private List<ItemSubResDTO> items;

}
