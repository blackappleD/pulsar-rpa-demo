package com.pul.demo.task;

import cn.hutool.json.JSONUtil;
import com.pul.demo.enums.WorldLevel;
import com.pul.demo.po.FF14ItemPO;
import com.pul.demo.po.FF14WorldPO;
import com.pul.demo.repo.FF14ItemRepo;
import com.pul.demo.repo.FF14WorldRepo;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 14:44
 */
@Slf4j
@Configuration
public class InitRunner implements ApplicationRunner {

	@Resource
	private FF14ItemRepo ff14ItemRepo;

	@Resource
	private FF14WorldRepo ff14WorldRepo;

	@Resource
	private ResourceLoader resourceLoader;

	@Value("${item.force-update:false}")
	private boolean itemUpdate;

	@Override
	public void run(ApplicationArguments args) {
		try {

			initItemData();
			initWorldData();
		} catch (IOException e) {
			log.error("初始化失败，读取初始化文件异常：{}", e.getMessage(), e);
		}

	}

	public void initWorldData() throws IOException {
		if (ff14WorldRepo.findById(1L).isPresent()) {
			return;
		}
		String regionStr = resourceLoader.getResource("classpath:init/region.json").getContentAsString(StandardCharsets.UTF_8);

		String worldsStr = resourceLoader.getResource("classpath:init/world.json").getContentAsString(StandardCharsets.UTF_8);

		List<Regin> dataCenters = JSONUtil.toList(regionStr, Regin.class);
		List<FF14WorldPO> worlds1 = dataCenters.stream().map(Regin::getRegion)
				.distinct()
				.map(r -> {
					FF14WorldPO world = new FF14WorldPO();
					world.setLevel(WorldLevel.REGION);
					world.setName(r);
					return world;
				}).toList();

		List<FF14WorldPO> worlds2 = dataCenters.stream()
				.map(Regin::getName)
				.distinct()
				.map(r -> {
					FF14WorldPO world = new FF14WorldPO();
					world.setLevel(WorldLevel.DATA_CENTER);
					world.setName(r);
					return world;
				}).toList();
		ff14WorldRepo.saveAll(worlds1);
		ff14WorldRepo.saveAll(worlds2);

		List<World> worldList = JSONUtil.toList(worldsStr, World.class);
		List<FF14WorldPO> world3 = worldList.stream().map(w -> {
			FF14WorldPO world = new FF14WorldPO();
			world.setWorldId(w.getId());
			world.setName(w.getName());
			world.setLevel(WorldLevel.WORLD);
			return world;
		}).toList();

		ff14WorldRepo.saveAll(world3);
		log.info("初始化世界数据完成！");
	}


	@Data
	public static class World {
		private Long id;
		private String name;
	}

	@Data
	public static class Regin {
		private String name;
		private String region;
	}


	public void initItemData() throws IOException {
		if (ff14ItemRepo.findById(1L).isPresent() && !itemUpdate) {
			return;
		}

		String json = resourceLoader.getResource("classpath:init/ff14itemsresponse.json").getContentAsString(StandardCharsets.UTF_8);

		Response bean = JSONUtil.toBean(json, Response.class);

		List<FF14ItemPO> poList = bean.getRows().stream().map(originalItem -> {
			FF14ItemPO item = new FF14ItemPO();
			item.setId(originalItem.getId());
			item.setName(originalItem.getName());
			return item;
		}).collect(Collectors.toList());

		ff14ItemRepo.saveAll(poList);
		log.info("初始化items数据完成");
	}

	@Data
	public static class Response {
		private List<OriginalItem> rows;

	}

	@Data
	public static class OriginalItem {
		private boolean isIndisposable;
		private boolean canBeHq;
		private boolean preview;
		private long gatherCount;
		private String itemType;
		private boolean isCrestWorthy;
		private long levelEquip;
		private boolean isUnique;
		private boolean craft;
		private String description;
		private long guYuan;
		private long levelItem;
		private long stackSize;
		private boolean isDyeable;
		private String name;
		private boolean isUntradable;
		private long recipeCount;
		private long id;
		private String job;
		private boolean npcTrade;
		private long classJobUse;
	}

}
