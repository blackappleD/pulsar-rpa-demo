package com.pul.demo.mapper;

import com.pul.demo.dto.UserSubscribeGroupReqDTO;
import com.pul.demo.dto.SubscribeGroupResDTO;
import com.pul.demo.po.FF14SubscribeGroupPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/26 15:58
 */
@Mapper(componentModel = "spring", uses = {FF14UserMapper.class, FF14ItemMapper.class})
public interface FF14SubscribeGroupMapper {

	SubscribeGroupResDTO po2resDto(FF14SubscribeGroupPO po);

	FF14SubscribeGroupPO reqDto2po(UserSubscribeGroupReqDTO dto);

	@Mapping(target = "po.id", ignore = true)
	void reqDto2po(UserSubscribeGroupReqDTO dto, @MappingTarget FF14SubscribeGroupPO po);

}
