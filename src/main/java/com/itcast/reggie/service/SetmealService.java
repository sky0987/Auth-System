package com.itcast.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcast.reggie.dto.DishDto;
import com.itcast.reggie.dto.SetmealDto;
import com.itcast.reggie.entity.Setmeal;


import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public  void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getByIdWithDish(long id);

    public void updateWithDish(SetmealDto setmealDto);

    public  void  delAll(long[]  ids);

}
