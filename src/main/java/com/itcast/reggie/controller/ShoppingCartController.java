package com.itcast.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.reggie.common.BaseContext;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.ShoppingCart;
import com.itcast.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 保存成功，返回当前保存的数据，用于订单处理
     * @param
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart  shoppingCart){
        //shoppingCart.setUserId((Long) session.getAttribute("user"));
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setCreateTime(LocalDateTime.now());

        //先查询当前购物车是否存在商品 根据当前用户id查询
        LambdaQueryWrapper<ShoppingCart>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //获取当前的菜品id去判断添加的是否是菜品还是套餐
        Long dishId = shoppingCart.getDishId();

        if (dishId!=null){
            //菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (cartServiceOne!=null){
            //说明当前购物车存在数据，当前商品+1；
            cartServiceOne.setNumber(cartServiceOne.getNumber()+1);
            //更新金额
            //BigDecimal multiply = BigDecimal.valueOf(cartServiceOne.getNumber()).multiply(cartServiceOne.getAmount());
            //cartServiceOne.setAmount(multiply);
            //更新当前购物车商品的数量
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //不存在 直接保存
            shoppingCartService.save(shoppingCart);
        }
        //判断同一dish更新商品的个数

        return  R.success("添加成功");
    }


    @GetMapping("/list")
    public  R<List<ShoppingCart>>  getAll(){
        LambdaQueryWrapper<ShoppingCart>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //userid查询
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return  R.success(list);
    }

   @PostMapping("/sub")
    public  R<String>  sub(@RequestBody  ShoppingCart  shoppingCart){
        //根据用户id去查询当前用户的购物车数据
       log.info("s:"+shoppingCart.getDishId()+"..."+shoppingCart.getSetmealId());
       LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
       //判断是菜品还是套餐去查询
       if (shoppingCart.getDishId()!=null){
           lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
       }else {
           lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
       }
       ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
       //查询出来去判断菜品个数
       if (cartServiceOne.getNumber()!=1){
           //菜品个数不是1的时候 个数减一
           cartServiceOne.setNumber(cartServiceOne.getNumber()-1);
           shoppingCartService.updateById(cartServiceOne);
       }else {
           //DELETE FROM shopping_cart WHERE (user_id = 1 AND dish_id = 1397849739276890114);
          shoppingCartService.remove(lambdaQueryWrapper);
       }
       return  R.success("修改成功");
   }


   @DeleteMapping("/clean")
    public  R<String>  delAll(){
        //清空购物车 删除当前用户下的所有数据
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return  R.success("删除成功");
   }
}
