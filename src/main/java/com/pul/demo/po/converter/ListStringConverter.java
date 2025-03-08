package com.pul.demo.po.converter;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chentong
 * @version 1.0
 * @description: description
 * @date 2024/12/19 11:10
 */
@Converter
public class ListStringConverter implements AttributeConverter<List<String>, String> {
	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if(attribute==null){
			return null;
		}
		return attribute.stream()
				.filter(ObjectUtil::isNotNull)
				.map(Objects::toString)
				.collect(Collectors.joining(","));
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (CharSequenceUtil.isBlank(dbData)) {
			return new ArrayList<>();
		}
		return Arrays.stream(dbData.split(","))
				.map(String::valueOf)
				.collect(Collectors.toList());
	}
}
