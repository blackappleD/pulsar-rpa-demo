package com.pul.demo.mapper;

import com.pul.demo.dto.SubscribeCfgReqDTO;
import com.pul.demo.dto.SubscribeCfgResDTO;
import com.pul.demo.po.FF14SubscribeCfgPO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FF14SubscribeCfgMapper {

	FF14SubscribeCfgPO dto2po(SubscribeCfgReqDTO dto);

	void dto2po(SubscribeCfgReqDTO dto, @MappingTarget FF14SubscribeCfgPO po);

	SubscribeCfgResDTO po2dto(FF14SubscribeCfgPO po);

}