package com.itcast.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.dto.DishDto;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.DishFlavor;
import com.itcast.reggie.mapper.DishMapper;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.DishFlavorService;
import com.itcast.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
   @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品，同时保存对应的口味数据
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息
        this.save(dishDto);

        //获取菜品id
        Long id = dishDto.getId();
        //菜品口味
        List<DishFlavor>  dishFlavors=dishDto.getFlavors();

        //遍历集合，在菜品口味中加入菜品id
        for (DishFlavor dishFlavor : dishFlavors) {
            dishFlavor.setDishId(id);
        }
        //保存菜品口味数据
        dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询当前菜品
        Dish dish = this.getById(id);

        DishDto  dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //获取口味数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);

        return dishDto;

    }

    @Override
    @Transactional
    public String updateWithFlavor(DishDto dishDto) {

       this.updateById(dishDto);


       //清理数据
       LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
       dishFlavorService.remove(lambdaQueryWrapper);

       //保存数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
        return "修改成功";
    }

    @Override
    @Transactional
    public void delAll(long[] ids) {
        //删除菜品信息
        List<Long> list=new ArrayList<>();
        for (long id : ids) {
            list.add(id);
        }
        this.removeByIds(list);

        //删除对应的口味信息
        for (long id : ids) {
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(lambdaQueryWrapper);
        }


    }
}
