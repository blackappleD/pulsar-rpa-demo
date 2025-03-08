package com.pul.demo.mapper;

import com.pul.demo.dto.ItemDTO;
import com.pul.demo.po.FF14ItemPO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FF14ItemMapper {
	ItemDTO po2dto(FF14ItemPO po);

	FF14ItemPO dto2po(ItemDTO dto);
}