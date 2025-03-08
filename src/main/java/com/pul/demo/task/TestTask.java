package com.pul.demo.task;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.pul.demo.dto.ItemDTO;
import com.pul.demo.po.FF14ItemPO;
import com.pul.demo.service.FF14ItemService;
import com.pul.demo.util.JsonUtil;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2025/1/15 12:01
 */
@Component
public class TestTask {

	@Resource
	private FF14ItemService ff14ItemService;

	private static final String URL_TEMPLATE = "https://www.garlandtools.org/db/doc/item/en/3/{}.json";

	public void test() throws InterruptedException {

		long start = System.currentTimeMillis();
		List<ItemDTO> items = ff14ItemService.searchItems("Everseeker's");
		String nameTemplate = "{}#{}";

		List<Cell> list = new ArrayList<>();
		for (ItemDTO item : items) {
			try (HttpResponse response = HttpUtil.createGet(CharSequenceUtil.format(URL_TEMPLATE, item.getId())).execute()) {
				String body = response.body();
				Attachment attachment = JsonUtil.fromJson(body, Attachment.class);
				Map<Integer, Attachment.CraftIngredient> map = attachment.getItem().getCraft().getFirst().getIngredients().stream()
						.filter(o -> o.getId() > 100)
						.collect(Collectors.toMap(Attachment.CraftIngredient::getId, Function.identity()));
				Map<Integer, List<Attachment.Craft>> ingCraftMap = attachment.getIngredients().stream()
						.filter(o -> Objects.nonNull(o.getCraft()))
						.collect(Collectors.toMap(Attachment.Ingredient::getId, Attachment.Ingredient::getCraft));

				List<Attachment.CraftIngredient> itemCrafts = new ArrayList<>(map.values()).stream()
						.sorted(Comparator.comparing(Attachment.CraftIngredient::getQuality)
								.thenComparing(Attachment.CraftIngredient::getId).reversed())
						.toList();
				for (int i = 0; i < itemCrafts.size(); i++) {
					Attachment.CraftIngredient craftIngredient = itemCrafts.get(i);
					Cell cell = new Cell();
					list.add(cell);
					if (i == 0) {
						cell.setEquipmentName(CharSequenceUtil.format(nameTemplate, item.getName(), item.getId()));
					}
					FF14ItemPO byId = ff14ItemService.findById((long) craftIngredient.getId());
					cell.setIngredientName(CharSequenceUtil.format(nameTemplate, byId.getName(), byId.getId()));
					if (craftIngredient.getId() > 100) {
						if (craftIngredient.getQuality() > 0) {
							cell.setIngredientCount(String.valueOf(craftIngredient.getAmount()));
							List<Attachment.CraftIngredient> craftIngredients = ingCraftMap.get(craftIngredient.getId()).getFirst().getIngredients();
							for (int l = 0; l < craftIngredients.size(); l++) {

								Attachment.CraftIngredient craftIngredientL = craftIngredients.get(l);
								if (craftIngredientL.getId() > 100) {
									FF14ItemPO byId1 = ff14ItemService.findById((long) craftIngredientL.getId());
									if (l == 0) {
										cell.setMaterialName(CharSequenceUtil.format(nameTemplate, byId1.getName(), byId1.getId()));
										cell.setMaterialCount(String.valueOf(craftIngredientL.getAmount() * craftIngredient.getAmount()));
									} else {
										Cell cell1 = new Cell();
										list.add(cell1);
										cell1.setMaterialName(CharSequenceUtil.format(nameTemplate, byId1.getName(), byId1.getId()));
										cell1.setMaterialCount(String.valueOf(craftIngredientL.getAmount() * craftIngredient.getAmount()));
									}
								}
							}
						} else {
							cell.setMaterialCount(String.valueOf(craftIngredient.getAmount()));
						}
					}
				}
			}
			Thread.sleep(RandomUtil.randomInt(1000, 1500));
		}
		try (ExcelWriter excelWriter = EasyExcel.write("C:\\Users\\achen\\Desktop\\ff14_720hq.xlsx").build()) {

			WriteSheet sheet1 = EasyExcel.writerSheet(0, "Sheet1").head(Cell.class).build();
			excelWriter.write(list, sheet1);


			Map<String, StCell> map = new HashMap<>();
			list.forEach(cell -> {
				String name;
				long count = Long.parseLong(cell.getMaterialCount());
				if (CharSequenceUtil.isBlank(cell.getMaterialName())) {
					name = cell.getIngredientName();
				} else {
					name = cell.getMaterialName();
				}
				if (map.containsKey(name)) {
					StCell stCell = map.get(name);
					stCell.setCount(String.valueOf(Long.parseLong(stCell.getCount()) + count));
					map.put(name, stCell);
				} else {
					map.put(name, new StCell(name, String.valueOf(count)));
				}
			});

			WriteSheet sheet2 = EasyExcel.writerSheet(1, "材料统计").head(StCell.class).build();
			excelWriter.write(map.values(), sheet2);
			excelWriter.finish();
		}
		System.out.println(CharSequenceUtil.format("处理结束！总耗时{}ms", System.currentTimeMillis() - start));
	}


	public static <T> List<T> readExcel(String filePath, Class<T> clazz) {
		return EasyExcel.read(new File(filePath))
				.headRowNumber(1)
				.head(clazz)
				.sheet(0)
				.doReadSync();
	}

	@Data
	@AllArgsConstructor
	public static class StCell {

		@ExcelProperty("材料名")
		private String name;

		@ExcelProperty("数量")
		private String count;

	}


	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Data
	public static class Cell {

		@ExcelProperty("装备名称")
		@ColumnWidth(20)
		private String equipmentName = "";

		@ExcelProperty("半成品名称")
		@ColumnWidth(20)
		private String ingredientName = "";

		@ExcelProperty("半成品数量")
		@ColumnWidth(5)
		private String ingredientCount = "";

		@ExcelProperty("材料名称")
		@ColumnWidth(20)
		private String materialName;

		@ExcelProperty("材料数量")
		@ColumnWidth(5)
		private String materialCount = "";
	}

	public static final String s = "{\"item\":{\"name\":\"Everseeker's Saw\",\"jobCategories\":\"CRP\",\"id\":44411,\"patch\":7.1,\"patchCategory\":1,\"price\":99999,\"ilvl\":720,\"category\":12,\"dyecount\":0,\"tradeable\":1,\"sell_price\":709,\"rarity\":1,\"convertable\":1,\"stackSize\":1,\"repair\":9,\"equip\":1,\"sockets\":1,\"repair_item\":33916,\"glamourous\":1,\"slot\":1,\"elvl\":100,\"jobs\":9,\"models\":[\"5005-1-7-0\",\"5081-2-1-0\"],\"attr\":{\"Physical Damage\":72,\"Magic Damage\":43,\"Delay\":2.8,\"Craftsmanship\":1433,\"Control\":743,\"CP\":0},\"attr_hq\":{\"Physical Damage\":72,\"Magic Damage\":43,\"Craftsmanship\":1624,\"Control\":842},\"attr_max\":{\"Strength\":290,\"Dexterity\":290,\"Vitality\":390,\"Intelligence\":414,\"Mind\":414,\"Piety\":190,\"GP\":10,\"CP\":8,\"Physical Damage\":143,\"Magic Damage\":143,\"Tenacity\":271,\"Direct Hit Rate\":271,\"Critical Hit\":271,\"Fire Resistance\":19,\"Ice Resistance\":19,\"Wind Resistance\":19,\"Earth Resistance\":19,\"Lightning Resistance\":19,\"Water Resistance\":19,\"Determination\":271,\"Skill Speed\":271,\"Spell Speed\":271,\"Slow Resistance\":4,\"Petrification Resistance\":4,\"Paralysis Resistance\":4,\"Silence Resistance\":4,\"Blind Resistance\":4,\"Poison Resistance\":4,\"Stun Resistance\":4,\"Sleep Resistance\":4,\"Bind Resistance\":4,\"Heavy Resistance\":4,\"Doom Resistance\":4,\"Craftsmanship\":1911,\"Control\":990,\"Gathering\":1864,\"Perception\":1065},\"icon\":\"t/39011\",\"sharedModels\":[19590,21208,24821,28461,35383,39825,43242],\"craft\":[{\"id\":35959,\"job\":9,\"rlvl\":720,\"durability\":70,\"quality\":17600,\"progress\":8050,\"lvl\":100,\"materialQualityFactor\":50,\"stars\":2,\"hq\":1,\"controlReq\":4450,\"craftsmanshipReq\":4780,\"unlockId\":44124,\"ingredients\":[{\"id\":44847,\"amount\":2,\"quality\":892.39435},{\"id\":44846,\"amount\":1,\"quality\":892.39435},{\"id\":44147,\"amount\":3,\"quality\":880.0},{\"id\":44062,\"amount\":1,\"quality\":842.8169},{\"id\":44848,\"amount\":2},{\"id\":44036,\"amount\":1},{\"id\":14,\"amount\":3},{\"id\":17,\"amount\":3}],\"complexity\":{\"nq\":3950,\"hq\":3970}}],\"downgrades\":[43263]},\"ingredients\":[{\"name\":\"Alexandrian Plate\",\"id\":44847,\"icon\":\"t/20975\",\"category\":49,\"ilvl\":720,\"price\":99999,\"craft\":[{\"id\":35958,\"job\":10,\"rlvl\":720,\"durability\":35,\"quality\":12848,\"progress\":3783,\"lvl\":100,\"materialQualityFactor\":0,\"stars\":2,\"hq\":1,\"controlReq\":4450,\"craftsmanshipReq\":4780,\"unlockId\":44125,\"ingredients\":[{\"id\":44845,\"amount\":4},{\"id\":44848,\"amount\":1},{\"id\":44035,\"amount\":1},{\"id\":15,\"amount\":3},{\"id\":17,\"amount\":3}],\"complexity\":{\"nq\":545,\"hq\":565}}]},{\"name\":\"Alexandrian Ore\",\"id\":44845,\"icon\":\"t/21491\",\"category\":48,\"ilvl\":720,\"price\":99999,\"nodes\":[1032]},{\"name\":\"Condensed Solution\",\"id\":44848,\"icon\":\"t/22701\",\"category\":54,\"ilvl\":720,\"price\":99999,\"desynthedFrom\":[44604,44603,44602,44601,44600,44599,44598,44597,44596,44595,44594,44593,44592,44591,44590,44589,44588,44587,44586,44585,44584,44583,44582,44581,44580,44579,44578,44577,44576,44575,44574,44573,44572,44571,44570,44569,44568,44567,44566,44565,44564,44563,44562,44561,44560,44559,44558,44557,44556,44555,44554,44553,44552,44550,44551],\"tradeShops\":[{\"shop\":\"Orange Scrip Exchange (Lv. 100 Materials/Furnishings)\",\"npcs\":[],\"listings\":[{\"item\":[{\"id\":\"44848\",\"amount\":1}],\"currency\":[{\"id\":\"41784\",\"amount\":125}]}]}]},{\"name\":\"Sungilt Aethersand\",\"id\":44035,\"icon\":21246,\"category\":54,\"ilvl\":690,\"price\":99999,\"reducedFrom\":[43933,43931,43731,43714,43829],\"tradeShops\":[{\"shop\":\"Orange Scrip Exchange (Lv. 100 Materials/Bait/Tokens)\",\"npcs\":[],\"listings\":[{\"item\":[{\"id\":\"44035\",\"amount\":1}],\"currency\":[{\"id\":\"41785\",\"amount\":100}]}]}]},{\"name\":\"Optical Nanofiber\",\"id\":44846,\"icon\":\"t/21690\",\"category\":54,\"ilvl\":720,\"price\":99999,\"craft\":[{\"id\":35957,\"job\":14,\"rlvl\":720,\"durability\":35,\"quality\":12848,\"progress\":3783,\"lvl\":100,\"materialQualityFactor\":0,\"stars\":2,\"hq\":1,\"controlReq\":4450,\"craftsmanshipReq\":4780,\"unlockId\":44129,\"ingredients\":[{\"id\":44844,\"amount\":4},{\"id\":44848,\"amount\":1},{\"id\":44035,\"amount\":1},{\"id\":19,\"amount\":3},{\"id\":18,\"amount\":3}],\"complexity\":{\"nq\":545,\"hq\":565}}]},{\"name\":\"Optical Fibergrass\",\"id\":44844,\"icon\":\"t/22699\",\"category\":54,\"ilvl\":720,\"price\":99999,\"nodes\":[1035]},{\"name\":\"Maraging Steel Ingot\",\"id\":44147,\"icon\":20833,\"category\":49,\"ilvl\":710,\"price\":99999,\"craft\":[{\"id\":35908,\"job\":9,\"rlvl\":710,\"durability\":35,\"quality\":12000,\"progress\":4125,\"lvl\":100,\"materialQualityFactor\":0,\"stars\":2,\"hq\":1,\"controlReq\":4400,\"craftsmanshipReq\":4740,\"unlockId\":44124,\"ingredients\":[{\"id\":44135,\"amount\":4},{\"id\":44142,\"amount\":2},{\"id\":14,\"amount\":3},{\"id\":17,\"amount\":3}],\"complexity\":{\"nq\":580,\"hq\":600}},{\"id\":35909,\"job\":10,\"rlvl\":710,\"durability\":35,\"quality\":12000,\"progress\":4125,\"lvl\":100,\"materialQualityFactor\":0,\"stars\":2,\"hq\":1,\"controlReq\":4400,\"craftsmanshipReq\":4740,\"unlockId\":44125,\"ingredients\":[{\"id\":44135,\"amount\":4},{\"id\":44142,\"amount\":2},{\"id\":15,\"amount\":3},{\"id\":17,\"amount\":3}],\"complexity\":{\"nq\":580,\"hq\":600}}]},{\"name\":\"Harmonite Ore\",\"id\":44135,\"icon\":21221,\"category\":48,\"ilvl\":710,\"price\":99999,\"nodes\":[1032]},{\"name\":\"Airbright Coolant\",\"id\":44142,\"icon\":22636,\"category\":54,\"ilvl\":710,\"price\":99999,\"tradeShops\":[{\"shop\":\"Allagan Tomestones of Aesthetics (Other)\",\"npcs\":[1049079],\"listings\":[{\"item\":[{\"id\":\"44142\",\"amount\":1}],\"currency\":[{\"id\":\"46\",\"amount\":20}]}]}]},{\"name\":\"Gargantua Leather\",\"id\":44062,\"icon\":22007,\"category\":52,\"ilvl\":680,\"price\":99999,\"craft\":[{\"id\":5647,\"job\":12,\"rlvl\":680,\"durability\":40,\"quality\":11000,\"progress\":3000,\"lvl\":98,\"materialQualityFactor\":0,\"hq\":1,\"quickSynth\":1,\"ingredients\":[{\"id\":44057,\"amount\":4},{\"id\":44052,\"amount\":1},{\"id\":11,\"amount\":8}],\"complexity\":{\"nq\":136,\"hq\":156}}],\"leves\":[1736,1737]},{\"name\":\"Gargantua Hide\",\"id\":44057,\"icon\":21814,\"category\":52,\"ilvl\":680,\"price\":99999,\"tradeShops\":[{\"shop\":\"Shop\",\"npcs\":[],\"listings\":[{\"item\":[{\"id\":\"44057\",\"amount\":1}],\"currency\":[{\"id\":\"26807\",\"amount\":3}]}]}],\"ventures\":[1042]},{\"name\":\"Acacia Bark\",\"id\":44052,\"icon\":22418,\"category\":50,\"ilvl\":670,\"price\":99999,\"ventures\":[1017],\"nodes\":[990]},{\"name\":\"Mythloam Aethersand\",\"id\":44036,\"icon\":21234,\"category\":54,\"ilvl\":690,\"price\":99999,\"reducedFrom\":[43932],\"tradeShops\":[{\"shop\":\"Orange Scrip Exchange (Lv. 100 Materials/Bait/Tokens)\",\"npcs\":[],\"listings\":[{\"item\":[{\"id\":\"44036\",\"amount\":1}],\"currency\":[{\"id\":\"41785\",\"amount\":200}]}]}]}],\"partials\":[{\"type\":\"item\",\"id\":\"44125\",\"obj\":{\"i\":44125,\"n\":\"Master Armorer XI\",\"l\":1,\"c\":26158,\"t\":63}},{\"type\":\"item\",\"id\":\"44845\",\"obj\":{\"i\":44845,\"n\":\"Alexandrian Ore\",\"l\":720,\"c\":\"t/21491\",\"t\":48}},{\"type\":\"item\",\"id\":\"44848\",\"obj\":{\"i\":44848,\"n\":\"Condensed Solution\",\"l\":720,\"c\":\"t/22701\",\"t\":54}},{\"type\":\"item\",\"id\":\"44035\",\"obj\":{\"i\":44035,\"n\":\"Sungilt Aethersand\",\"l\":690,\"c\":21246,\"t\":54}},{\"type\":\"node\",\"id\":\"1032\",\"obj\":{\"i\":1032,\"n\":\"The Knowable\",\"l\":100,\"t\":0,\"z\":4510,\"s\":1,\"lt\":\"Legendary\",\"ti\":[6,18]}},{\"type\":\"item\",\"id\":\"41784\",\"obj\":{\"i\":41784,\"n\":\"Orange Crafters' Scrip\",\"l\":1,\"c\":\"t/65110\",\"t\":100}},{\"type\":\"item\",\"id\":\"41785\",\"obj\":{\"i\":41785,\"n\":\"Orange Gatherers' Scrip\",\"l\":1,\"c\":65109,\"t\":100}},{\"type\":\"item\",\"id\":\"43933\",\"obj\":{\"i\":43933,\"n\":\"Goldbranch\",\"l\":670,\"c\":22416,\"t\":54}},{\"type\":\"item\",\"id\":\"43931\",\"obj\":{\"i\":43931,\"n\":\"Electrocoal\",\"l\":670,\"c\":21205,\"t\":48}},{\"type\":\"item\",\"id\":\"43731\",\"obj\":{\"i\":43731,\"n\":\"Horned Frog\",\"l\":670,\"c\":28600,\"t\":47}},{\"type\":\"item\",\"id\":\"43714\",\"obj\":{\"i\":43714,\"n\":\"Shovelnose Catfish\",\"l\":670,\"c\":28589,\"t\":47}},{\"type\":\"item\",\"id\":\"43829\",\"obj\":{\"i\":43829,\"n\":\"Sunlit Prism\",\"l\":670,\"c\":28649,\"t\":47}},{\"type\":\"item\",\"id\":\"44129\",\"obj\":{\"i\":44129,\"n\":\"Master Alchemist XI\",\"l\":1,\"c\":26158,\"t\":63}},{\"type\":\"item\",\"id\":\"44844\",\"obj\":{\"i\":44844,\"n\":\"Optical Fibergrass\",\"l\":720,\"c\":\"t/22699\",\"t\":54}},{\"type\":\"node\",\"id\":\"1035\",\"obj\":{\"i\":1035,\"n\":\"Arena of Valor\",\"l\":100,\"t\":3,\"z\":4510,\"s\":1,\"lt\":\"Legendary\",\"ti\":[4,16]}},{\"type\":\"item\",\"id\":\"44124\",\"obj\":{\"i\":44124,\"n\":\"Master Blacksmith XI\",\"l\":1,\"c\":26158,\"t\":63}},{\"type\":\"item\",\"id\":\"44135\",\"obj\":{\"i\":44135,\"n\":\"Harmonite Ore\",\"l\":710,\"c\":21221,\"t\":48}},{\"type\":\"item\",\"id\":\"44142\",\"obj\":{\"i\":44142,\"n\":\"Airbright Coolant\",\"l\":710,\"c\":22636,\"t\":54}},{\"type\":\"npc\",\"id\":\"1049079\",\"obj\":{\"i\":1049079,\"n\":\"Zircon\",\"l\":4503,\"s\":6,\"t\":\"Tomestone Exchange\",\"r\":1,\"a\":4647,\"c\":[8.63,13.57]}},{\"type\":\"item\",\"id\":\"46\",\"obj\":{\"i\":46,\"n\":\"Allagan Tomestone of Aesthetics\",\"l\":1,\"c\":\"t/65107\",\"t\":63}},{\"type\":\"item\",\"id\":\"44057\",\"obj\":{\"i\":44057,\"n\":\"Gargantua Hide\",\"l\":680,\"c\":21814,\"t\":52}},{\"type\":\"item\",\"id\":\"44052\",\"obj\":{\"i\":44052,\"n\":\"Acacia Bark\",\"l\":670,\"c\":22418,\"t\":50}},{\"type\":\"item\",\"id\":\"26807\",\"obj\":{\"i\":26807,\"n\":\"Bicolor Gemstone\",\"l\":1,\"c\":65071,\"t\":100}},{\"type\":\"node\",\"id\":\"990\",\"obj\":{\"i\":990,\"n\":\"Pyariyoanaan Plain\",\"l\":100,\"t\":2,\"z\":4508}},{\"type\":\"item\",\"id\":\"43932\",\"obj\":{\"i\":43932,\"n\":\"Brightwind Ore\",\"l\":690,\"c\":21212,\"t\":48}},{\"type\":\"item\",\"id\":\"19590\",\"obj\":{\"i\":19590,\"n\":\"Millking's Saw\",\"l\":300,\"c\":35027,\"t\":12}},{\"type\":\"item\",\"id\":\"21208\",\"obj\":{\"i\":21208,\"n\":\"Augmented Millking's Saw\",\"l\":330,\"c\":35027,\"t\":12}},{\"type\":\"item\",\"id\":\"24821\",\"obj\":{\"i\":24821,\"n\":\"Blessed Millking's Saw\",\"l\":350,\"c\":35027,\"t\":12,\"p\":1000}},{\"type\":\"item\",\"id\":\"28461\",\"obj\":{\"i\":28461,\"n\":\"Facet Saw\",\"l\":460,\"c\":35035,\"t\":12}},{\"type\":\"item\",\"id\":\"35383\",\"obj\":{\"i\":35383,\"n\":\"Chondrite Saw\",\"l\":560,\"c\":35044,\"t\":12,\"p\":51087}},{\"type\":\"item\",\"id\":\"39825\",\"obj\":{\"i\":39825,\"n\":\"Afflatus Saw\",\"l\":620,\"c\":39002,\"t\":12}},{\"type\":\"item\",\"id\":\"43242\",\"obj\":{\"i\":43242,\"n\":\"Titanium Gold Saw\",\"l\":670,\"c\":39007,\"t\":12,\"p\":51923}},{\"type\":\"item\",\"id\":\"44847\",\"obj\":{\"i\":44847,\"n\":\"Alexandrian Plate\",\"l\":720,\"c\":\"t/20975\",\"t\":49}},{\"type\":\"item\",\"id\":\"44846\",\"obj\":{\"i\":44846,\"n\":\"Optical Nanofiber\",\"l\":720,\"c\":\"t/21690\",\"t\":54}},{\"type\":\"item\",\"id\":\"44147\",\"obj\":{\"i\":44147,\"n\":\"Maraging Steel Ingot\",\"l\":710,\"c\":20833,\"t\":49}},{\"type\":\"item\",\"id\":\"44062\",\"obj\":{\"i\":44062,\"n\":\"Gargantua Leather\",\"l\":680,\"c\":22007,\"t\":52}},{\"type\":\"item\",\"id\":\"44036\",\"obj\":{\"i\":44036,\"n\":\"Mythloam Aethersand\",\"l\":690,\"c\":21234,\"t\":54}},{\"type\":\"item\",\"id\":\"43263\",\"obj\":{\"i\":43263,\"n\":\"Ra'Kaznar Saw\",\"l\":690,\"c\":39008,\"t\":12}}]}";

	@Data
	public static class Attachment {
		private Item item;
		private List<Ingredient> ingredients;
		private List<Partial> partials;

		@Data
		public static class Item {
			private String name;
			private String jobCategories;
			private int id;
			private double patch;
			private int patchCategory;
			private int price;
			private int ilvl;
			private int category;
			private int dyecount;
			private int tradeable;
			private int sell_price;
			private int rarity;
			private int convertable;
			private int stackSize;
			private int repair;
			private int equip;
			private int sockets;
			private int repair_item;
			private int glamourous;
			private int slot;
			private int elvl;
			private int jobs;
			private List<String> models;
			private Map<String, Integer> attr;
			private Map<String, Integer> attr_hq;
			private Map<String, Integer> attr_max;
			private String icon;
			private List<Integer> sharedModels;
			private List<Craft> craft;
			private List<Integer> downgrades;

		}

		@Data
		public static class Ingredient {
			private String name;
			private int id;
			private String icon;
			private int category;
			private int ilvl;
			private int price;
			private List<Craft> craft;
			private List<Integer> nodes;
			private List<Integer> desynthedFrom;
			private List<TradeShop> tradeShops;
			private List<Integer> reducedFrom;
			private List<Integer> ventures;
			private List<Voyage> voyages;

		}

		@Data
		public static class Craft {
			private int id;
			private int job;
			private int rlvl;
			private int durability;
			private int quality;
			private int progress;
			private int lvl;
			private int materialQualityFactor;
			private int stars;
			private int hq;
			private int controlReq;
			private int craftsmanshipReq;
			private int unlockId;
			private List<CraftIngredient> ingredients;
			private Complexity complexity;
			private int quickSynth;

		}

		@Data
		public static class CraftIngredient {
			private int id;
			private int amount;
			private double quality;
		}

		@Data
		public static class Complexity {
			private int nq;
			private int hq;

		}

		@Data
		public static class TradeShop {
			private String shop;
			private List<Integer> npcs;
			private List<Listing> listings;

		}

		@Data
		public static class Listing {
			private List<ListingItem> item;
			private List<ListingItem> currency;
		}

		@Data
		public static class ListingItem {
			private String id;
			private int amount;

		}

		@Data
		public static class Voyage {
			private int id;
			private int type;

		}

		@Data
		public static class Partial {
			private String type;
			private String id;
//			private PartialObj obj;

		}

		@Data
		public static class PartialObj {
			private String i;
			private String n;
			private String l;
			private String c;
			private String t;
			private Integer z;
			private Integer s;
			private String lt;
			private List<Integer> ti;
			private Integer r;
			private Integer a;
			private List<Double> coordinates;
		}

	}

}
