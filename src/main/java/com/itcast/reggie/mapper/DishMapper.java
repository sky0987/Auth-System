package com.itcast.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcast.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
