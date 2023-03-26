package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.common.R;
import com.itcast.reggie.dto.SetmealDto;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.entity.SetmealDish;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.SetmealDishService;
import com.itcast.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 套餐管理
 *
 *
 *  关于springCache
 * @CacheEvict  用于增删改   清理缓存
 * @Cacheable  用于查   如果spring中有缓存，直接返回缓存数据，如果没有，那么调用方法查询缓存数据
 *      condition 条件 满足才可以缓存
 *
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
@Autowired
private SetmealService setmealService;

@Autowired
private CategoryService categoryService;


@Autowired
private SetmealDishService setmealDishService;



   @GetMapping("/page")
    public R<Page>  getAll(int page,int pageSize,String name){
        Page<Setmeal> page1=new Page<>(page,pageSize);
        Page<SetmealDto>  page2=new Page<>();


        //根据姓名去分页查找
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1,lambdaQueryWrapper);

        BeanUtils.copyProperties(page1,page2,"records");
        List<Setmeal> records = page1.getRecords();
        List<Category> list = categoryService.list();
        List<SetmealDto> setmealDtos=new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);
            Long categoryId = record.getCategoryId();
            for (Category category : list) {
                Long id = category.getId();
                if (categoryId.equals(id)){
                    String name1 = category.getName();
                    setmealDto.setCategoryName(name1);
                    break;
                }
            }
            setmealDtos.add(setmealDto);
        }
       page2.setRecords(setmealDtos);

        return R.success(page2);

    }

    @PostMapping
    @CacheEvict(value = "setmeal",allEntries = true)  //删除全部缓存
    public R<String>  save(@RequestBody SetmealDto setmealDto){
       setmealService.saveWithDish(setmealDto);
       return R.success("添加成功");
    }

    @GetMapping("/{id}")
    @Cacheable(value = "setmeal",key = "#id")
    public R<SetmealDto>  get(@PathVariable long id){
        SetmealDto byIdWithDish = setmealService.getByIdWithDish(id);
        return  R.success(byIdWithDish);
    }

    @PutMapping
    @CacheEvict(value = "setmeal",key = "#setmealDto.id")
    public  R<String> update(@RequestBody  SetmealDto setmealDto){
       setmealService.updateWithDish(setmealDto);
       return R.success("修改成功");
    }


    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmeal",allEntries = true)
    public  R<String> updateStatus(@PathVariable Integer status,long[] ids){
        for (long id : ids) {
            Setmeal byId = setmealService.getById(id);
            byId.setStatus(status);
            setmealService.updateById(byId);
        }

        return  R.success("修改成功");
    }


    @DeleteMapping
    @CacheEvict(value = "setmeal",allEntries = true) //
    public   R<String> delAll(long[] ids){
        setmealService.delAll(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmeal",key = "#categoryId+'_'+#status")
    public R<List<Setmeal>> listR(long categoryId,String status){
         LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
         lambdaQueryWrapper.eq(Setmeal::getCategoryId,categoryId).eq(Setmeal::getStatus,status);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
       return R.success(list);
    }

    @GetMapping("/dish/{id}")
    @Cacheable(value = "setmeal",key = "#id")
    public  R<List<SetmealDish>> list(@PathVariable long id){
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
