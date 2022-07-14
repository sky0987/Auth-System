package com.itcast.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.reggie.dto.DishDto;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {

    public  void  saveWithFlavor(DishDto dishDto);

    public  DishDto getByIdWithFlavor(Long id);

    public   String  updateWithFlavor(DishDto dishDto);

    public  void  delAll(long[]  ids);
}
