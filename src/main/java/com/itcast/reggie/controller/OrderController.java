package com.itcast.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itcast.reggie.common.BaseContext;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.AddressBook;
import com.itcast.reggie.entity.OrderDetail;
import com.itcast.reggie.entity.Orders;
import com.itcast.reggie.entity.ShoppingCart;
import com.itcast.reggie.service.AddressBookService;
import com.itcast.reggie.service.OrderDetailService;
import com.itcast.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String>  save(@RequestBody Orders orders){
        System.out.println("s:"+orders);
        orderService.submit(orders);
        return  R.success("添加成功");
    }

    @GetMapping("/userPage")
    public R<Page> find(Integer page,Integer pageSize){
        Page<Orders> page1=new Page(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper=new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        orderService.page(page1,ordersLambdaQueryWrapper);
        return  R.success(page1);
    }

    @GetMapping("/page")
    public  R<Page> all(Integer page,Integer pageSize){
        Page page1=new Page(page,pageSize);
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(page1,lambdaQueryWrapper);
        return  R.success(page1);
    }
}