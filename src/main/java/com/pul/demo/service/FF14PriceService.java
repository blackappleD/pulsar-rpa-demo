package com.pul.demo.service;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.net.SSLContextBuilder;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.pul.demo.dto.ItemDTO;
import com.pul.demo.dto.ItemPriceInfo;
import com.pul.demo.dto.SubscribePriceGroup;
import com.pul.demo.exception.FF14Exception;
import com.pul.demo.po.FF14ItemSubPO;
import com.pul.demo.po.FF14SubscribeGroupPO;
import com.pul.demo.po.FF14UserPO;
import com.pul.demo.util.AdminUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/27 17:23
 */
@Slf4j
@Service
public class FF14PriceService {

	// 占位符： 大区 itemid
	private static final String UNIVERSAL_URI = "https://universalis.app/api/v2/{}/{}?listings={}&entries=20&noGst=1&hq={}";


	@Resource(name = "uniHttpReqExecutor")
	private ThreadPoolExecutor threadPoolExecutor;

	@Resource
	private FF14SubscribeGroupService ff14SubscribeGroupService;

	public List<ItemPriceInfo> requestItemPriceInfo(String worldName,
	                                                Integer itemId,
	                                                Boolean hq) {
		String url = CharSequenceUtil.format(UNIVERSAL_URI, worldName, itemId, 50, hq);
		return requestItemPriceInfo(url, worldName, null);
	}

	public List<SubscribePriceGroup> subscribeItemPriceOnTime() {
		return subscribeItemPrice(AdminUtil.getCurrentUser());

	}

	public List<SubscribePriceGroup> subscribeItemPrice(FF14UserPO user) {
		List<FF14SubscribeGroupPO> userSubscribeList = ff14SubscribeGroupService.findByUser(user);

		return userSubscribeList.stream().map(userSubscribe -> {
			SubscribePriceGroup data = new SubscribePriceGroup();
			data.setId(userSubscribe.getId());
			data.setWorldName(userSubscribe.getWorld().getName());
			data.setItemPriceGroups(requestItemPriceInfo(userSubscribe.getItems(), userSubscribe.getWorld().getName()));
			return data;
		}).toList();
	}

	public List<SubscribePriceGroup.ItemPriceGroup> requestItemPriceInfo(List<FF14ItemSubPO> itemSubs, String worldName) {

		List<SubscribePriceGroup.ItemPriceGroup> list = ListUtil.list(false);
		List<FF14ItemSubPO> hqItemSubs = itemSubs.stream().filter(FF14ItemSubPO::getHq).toList();
		List<FF14ItemSubPO> notHqItemSubs = itemSubs.stream().filter(ff14ItemSubPO -> !ff14ItemSubPO.getHq()).toList();

		Map<String, FF14ItemSubPO> itemIdNameMap = itemSubs.stream()
				.collect(Collectors.toMap(itemSub -> String.valueOf(itemSub.getItem().getId()),
						Function.identity()));

		if (hqItemSubs.size() == 1) {
			list.add(SubscribePriceGroup.ItemPriceGroup.builder()
					.id(hqItemSubs.getFirst().getItem().getId())
					.name(hqItemSubs.getFirst().getItem().getName())
					.itemPriceInfoList(requestItemPriceInfo(hqItemSubs.getFirst(), worldName))
					.build());
		} else {
			String hqItemIdStr = itemSubs.stream()
					.filter(FF14ItemSubPO::getHq)
					.map(itemSub -> String.valueOf(itemSub.getItem().getId()))
					.collect(Collectors.joining(","));
			String hqUrl = CharSequenceUtil.format(UNIVERSAL_URI, worldName, hqItemIdStr, 5, true);
			list.addAll(request(itemIdNameMap, worldName, hqUrl));
		}
		if (notHqItemSubs.size() == 1) {
			list.add(SubscribePriceGroup.ItemPriceGroup.builder()
					.id(notHqItemSubs.getFirst().getItem().getId())
					.name(notHqItemSubs.getFirst().getItem().getName())
					.itemPriceInfoList(requestItemPriceInfo(notHqItemSubs.getFirst(), worldName))
					.build());
		} else {
			String notHqItemIdStr = itemSubs.stream()
					.filter(itemSub -> !itemSub.getHq())
					.map(itemSub -> String.valueOf(itemSub.getItem().getId()))
					.collect(Collectors.joining(","));

			String nqUrl = CharSequenceUtil.format(UNIVERSAL_URI, worldName, notHqItemIdStr, 5, "");
			list.addAll(request(itemIdNameMap, worldName, nqUrl));
		}
		return list;

	}


	private List<SubscribePriceGroup.ItemPriceGroup> request(Map<String, FF14ItemSubPO> itemIdNameMap, String worldName, String hqUrl) {
		try (HttpResponse response = submitRequest(hqUrl)) {
			if (response.getStatus() == 200) {

				String body = response.body();
				Map<String, Listings> itemListingsMap = JSONUtil.toBean(body, BatchItemsPrice.class).getItems();
				return itemListingsMap.entrySet().stream().map(entry -> {
					SubscribePriceGroup.ItemPriceGroup itemPriceGroup = new SubscribePriceGroup.ItemPriceGroup();
					itemPriceGroup.setId(Long.valueOf(entry.getKey()));
					FF14ItemSubPO itemSub = itemIdNameMap.get(entry.getKey());
					itemPriceGroup.setName(itemSub.getItem().getName());
					List<ItemPriceInfo> listings = entry.getValue().getListings();
					listings.forEach(listing -> {
						if (Objects.nonNull(itemSub.getNotifyThreshold())) {
							listing.setLowerThreshold(listing.getPricePerUnit() <= itemSub.getNotifyThreshold());
						}
						if (CharSequenceUtil.isBlank(listing.getWorldName())) {
							listing.setWorldName(worldName);
						}
						listing.setTotal(listing.getTotal() + listing.getTax());
					});
					itemPriceGroup.setItemPriceInfoList(listings);
					return itemPriceGroup;
				}).toList();
			}
		}
		return new ArrayList<>();
	}

	public List<ItemPriceInfo> requestItemPriceInfo(FF14ItemSubPO itemSub, String worldName) {

		String url = CharSequenceUtil.format(UNIVERSAL_URI, worldName, itemSub.getItem().getId(), 5, itemSub.getHq());
		return requestItemPriceInfo(url, worldName, itemSub.getNotifyThreshold());
	}

	private List<ItemPriceInfo> requestItemPriceInfo(String url, String worldName, Long notifyThreshold) {
		try (HttpResponse response = submitRequest(url)) {
			if (response.getStatus() == 200) {
				String body = response.body();
				List<ItemPriceInfo> listings = JSONUtil.toBean(body, Listings.class).getListings();
				listings.forEach(listing -> {
					if (Objects.nonNull(notifyThreshold)) {
						listing.setLowerThreshold(listing.getPricePerUnit() <= notifyThreshold);
					}
					if (CharSequenceUtil.isBlank(listing.getWorldName())) {
						listing.setWorldName(worldName);
					}
					listing.setTotal(listing.getTotal() + listing.getTax());
				});
				return listings;
			} else {
				return new ArrayList<>();
			}
		}
	}

	private HttpResponse submitRequest(String url) {
		return submitRequest(url, 3); // 默认重试3次
	}

	private HttpResponse submitRequest(String url, int retryTimes) {
		try {
			Future<HttpResponse> submit = threadPoolExecutor.submit(() -> {
				log.info("请求universalis api：{}", url);
				return HttpUtil.createGet(url)
						.setSSLSocketFactory(SSLContextBuilder.create().setProtocol("TLSv1.2").build().getSocketFactory())
						.timeout(60000)
						.execute();
			});
			return submit.get();
		} catch (Exception e) {
			if (retryTimes > 0) {
				try {
					log.warn("请求universalis api异常:{}，剩余重试次数{}，准备重新调用！", e.getMessage(), retryTimes);
					// 指数退避策略，每次增加等待时间
					Thread.sleep(1000L * (4 - retryTimes));
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt(); // 保持中断状态
					throw new FF14Exception("请求被中断");
				}
				return submitRequest(url, retryTimes - 1);
			} else {
				log.error("调用universalis api达到最大重试次数，最终失败");
				throw new FF14Exception("调用universalis api异常", e);
			}
		}
	}

	@Data
	public static class BatchItemsPrice {
		private List<Long> itemIds;

		// itemId, listings
		private Map<String, Listings> items;
	}

	@Data
	public static class Rows {
		public List<ItemDTO> rows;
	}

	@Data
	public static class Listings {
		public List<ItemPriceInfo> listings;
	}

}
