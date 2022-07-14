package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.common.R;
import com.itcast.reggie.dto.DishDto;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Dish;
import com.itcast.reggie.entity.DishFlavor;
import com.itcast.reggie.service.CategoryService;
import com.itcast.reggie.service.DishFlavorService;
import com.itcast.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
  @Autowired
  private DishService dishService;
  @Autowired
  private DishFlavorService dishFlavorService;
  @Autowired
  private CategoryService categoryService;

  @PostMapping
  public R<String> save(@RequestBody DishDto dishDto) {
    dishService.saveWithFlavor(dishDto);
    return R.success("添加成功");
  }

 /* @GetMapping("/page")
  public R<Page> page(int page, int pageSize, String name) {
    Page<Dish> pageInfo=new Page<>(page,pageSize);
    Page<DishDto> dishDtoPage=new Page<>();

    LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    lambdaQueryWrapper.like(name!=null,Dish::getName,name);
    lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
    dishService.page(pageInfo,lambdaQueryWrapper);

    //对象拷贝
    BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
    List<Dish> records = pageInfo.getRecords();
    List<DishDto> list=new ArrayList<>();

    for (Dish record : records) {
      DishDto dishDto=new DishDto(); //查出来一个，封装一个对象，添加到集合
      BeanUtils.copyProperties(record,dishDto);
      Long categoryId = record.getCategoryId();//分类id
      Category category = categoryService.getById(categoryId);
      String name1 = category.getName();
      dishDto.setCategoryName(name1);
      list.add(dishDto);
    }

    dishDtoPage.setRecords(list);

    return R.success(dishDtoPage);


  }*/
  @GetMapping("/page")
    public  R<Page>  page(int page, int pageSize, String name){
      Page<Dish>  pageInfo=new Page<>(page,pageSize);
      Page<DishDto> dishDtoPage=new Page<>();

    LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
    lambdaQueryWrapper.like(name!=null,Dish::getName,name);
    lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
    dishService.page(pageInfo,lambdaQueryWrapper);
/**
 * 获取菜品的所有信息
 */
    BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
    List<Dish> records = pageInfo.getRecords();
    List<Category> all = categoryService.list();
    List<DishDto> list=new ArrayList<>();//因为从page中获取的records是list集合，所以重新设置的也是list集合
    for (Dish record : records) {
      DishDto  dishDto=new DishDto();
      BeanUtils.copyProperties(record,dishDto);
      Long categoryId = record.getCategoryId();
      for (Category category : all) {
        Long id = category.getId();
        if (categoryId.equals(id)) {
          String name1 = category.getName();
          dishDto.setCategoryName(name1);
          break;
        }
      }
      list.add(dishDto);  //添加到新的集合
    }
    dishDtoPage.setRecords(list);//重新设置page中records集合


    return  R.success(dishDtoPage);
    }



    @GetMapping("/{id}")
    public  R<DishDto>  getById(@PathVariable long id){
      DishDto byId = dishService.getByIdWithFlavor(id);
     // System.out.println(byId);
      return R.success(byId);
    }

    @PutMapping
    public  R<String>  update(@RequestBody DishDto dishDto){
      String s = dishService.updateWithFlavor(dishDto);

      return R.success(s);
    }

    @PostMapping("/status/{status}")
    public  R<String> updateStatus(@PathVariable Integer status,long[] ids){
      for (long id : ids) {
        Dish byId = dishService.getById(id);
        byId.setStatus(status);
        dishService.updateById(byId);
      }

    return  R.success("修改成功");
    }

    @DeleteMapping
    public   R<String> delAll(long[] ids){
     dishService.delAll(ids);
    return R.success("删除成功");
    }

    @GetMapping("/list")
    public  R<List<DishDto>>  getS(long categoryId){
      //根据id查询，查询出来的数据不止一个
      LambdaQueryWrapper<Dish>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
      lambdaQueryWrapper.eq(Dish::getStatus,1);
      //根据分类id查询所有的菜品数据
      List<Dish> list = dishService.list(lambdaQueryWrapper);

      List<DishDto> dishDtos=new ArrayList<>();
      //一个菜品对应多个口味
      for (Dish dish : list) {
        //dish一个菜品
        DishDto  dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //根据id查询所有口味数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1=new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper1);
        //添加口味到当前对象
        dishDto.setFlavors(list1);
        //添加对象到集合
        dishDtos.add(dishDto);
      }
      return R.success(dishDtos);
    }
}
