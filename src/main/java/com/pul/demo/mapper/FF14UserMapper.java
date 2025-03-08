package com.pul.demo.mapper;

import com.pul.demo.dto.UserRegisterReqDTO;
import com.pul.demo.dto.UserResDTO;
import com.pul.demo.po.FF14UserPO;
import org.mapstruct.Mapper;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/20 9:38
 */
@Mapper(componentModel = "spring")
public interface FF14UserMapper {

	FF14UserPO dto2po(UserRegisterReqDTO dto);

	UserResDTO po2dto(FF14UserPO po);

}
