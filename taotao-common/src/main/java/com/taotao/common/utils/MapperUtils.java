package com.taotao.common.utils;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper 映射工具类
 * @Description: 映射工具类
 */
public class MapperUtils
{
	/**
	 * Mapper 映射
	 * @param <T> 泛型类
	 * @param source 源
	 * @param clazz 描述类
	 * @return 泛型实体结果
	 */
	public static <T> T mapTo(Object source, Class<T> clazz)
	{
		ModelMapper modelMapper = new ModelMapper();
		T result = modelMapper.map(source, clazz);

		return result;
	}

	/**
	 * Mapper 映射列表.
	 * @param <T> 泛型类
	 * @param sources 源列表
	 * @param clazz 描述类
	 * @return 泛型实体结果列表
	 */
	public static <T> List<T> mapTo(List<?> sources, Class<T> clazz)
	{
		List<T> result = new ArrayList<T>();

		ModelMapper modelMapper = new ModelMapper();

		for(Object item : sources)
		{
			T entry = modelMapper.map(item, clazz);
			result.add(entry);
		}

		return result;
	}
}
