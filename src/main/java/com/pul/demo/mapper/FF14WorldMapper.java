package com.pul.demo.mapper;

import com.pul.demo.dto.WorldDTO;
import com.pul.demo.po.FF14WorldPO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FF14WorldMapper {
    WorldDTO po2dto(FF14WorldPO po);
}