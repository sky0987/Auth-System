package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.Category;
import com.itcast.reggie.entity.Employee;
import com.itcast.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public  R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 查询全部菜品分类
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public  R<Page> getAll(int page,int pageSize){
        Page page1=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.orderByDesc(Category::getSort);

        Page page2 = categoryService.page(page1, queryWrapper);

        return R.success(page1);

    }

    /**
     * 修改菜品分类
     * @param category
     * @return
     */
    @PutMapping
    public  R<String> update(@RequestBody Category category){
        boolean b = categoryService.updateById(category);
        if (b){
            return  R.success("修改成功");
        }else {
            return  R.error("修改失败");

        }

    }

    /**
     * 修改时回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public  R<Category> getById(@PathVariable Integer id){
        Category byId = categoryService.getById(id);
        return R.success(byId);
    }

    @DeleteMapping
    private  R<String> del(@RequestParam("ids") Long id){
        //判断当前的分类是否包含分类或者套餐
        categoryService.remove(id);
        return  R.success("删除成功");
    }


    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    public  R<List<Category>>  list(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        System.out.println(list.toString());
        return  R.success(list);
    }
}
