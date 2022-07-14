package com.itcast.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.dto.DishDto;
import com.itcast.reggie.dto.SetmealDto;
import com.itcast.reggie.entity.DishFlavor;
import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.entity.SetmealDish;
import com.itcast.reggie.mapper.SetmealMapper;
import com.itcast.reggie.service.SetmealDishService;
import com.itcast.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
@Autowired
private SetmealDishService dishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long id = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
              setmealDish.setSetmealId(id);
        }
       dishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public SetmealDto getByIdWithDish(long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = dishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return  setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {

        this.updateById(setmealDto);
        log.info("s"+setmealDto.toString());

        //删除套餐菜品数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        dishService.remove(lambdaQueryWrapper);

        //新增套餐菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
              setmealDish.setSetmealId(setmealDto.getId());
        }
        dishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void delAll(long[] ids) {
        //删除套餐信息
        List<Long> list=new ArrayList<>();
        for (long id : ids) {
            list.add(id);
        }
        this.removeByIds(list);

        //删除对应的口味信息
        for (long id : ids) {
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
            dishService.remove(lambdaQueryWrapper);
        }
    }
}
