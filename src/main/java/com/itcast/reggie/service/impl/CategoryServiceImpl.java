package com.itcast.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.common.exception.CustomException;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.mapper.CategoryMapper;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.DishService;
import com.itcast.reggie.service.SetmealService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

  @Autowired
    private DishService dishService;
  @Autowired
    private SetmealService service;


  @Autowired
  private CategoryService categoryService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */

    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(lambdaQueryWrapper);
        if (count>0){
            //抛出一个异常
            throw  new CustomException("当前分类下关联了菜品，无法删除");
        }

        //查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId,id);
        int count1 = service.count(lambdaQueryWrapper1);
        if (count1>0){
            throw  new CustomException("当前分类下关联了套餐，无法删除");
        }

        //正常删除
        super.removeById(id);

    }

}
