package com.itcast.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcast.reggie.common.BaseContext;
import com.itcast.reggie.common.exception.CustomException;
import com.itcast.reggie.entity.*;
import com.itcast.reggie.mapper.OrderMapper;
import com.itcast.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private  OrderService orderService;

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders) {
        //根据地址id查询出当前地址的信息
        AddressBook addressBook = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getId, orders.getAddressBookId()));
       //获取当前购物车的数据，根据当前的用户id
        List<ShoppingCart> list = shoppingCartService.list(
                new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, BaseContext.getCurrentId()));
      //查询用户数据
        User user = userService.getById(BaseContext.getCurrentId());

        AtomicInteger amount=new AtomicInteger(0);

        List<OrderDetail> orderDetails=new ArrayList<>();
        Long id=IdWorker.getId();

        for (ShoppingCart shoppingCart : list) {
            OrderDetail orderDetail=new OrderDetail();
            //将购物车的数据全部复制到订货单明细表之中
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            //计算总金额
            amount.addAndGet(shoppingCart.getAmount().multiply(BigDecimal.valueOf(shoppingCart.getNumber())).intValue());
            orderDetail.setOrderId(id);
            orderDetails.add(orderDetail);
        }
        //订单表出入数据
        orders.setId(id);
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setConsignee(addressBook.getConsignee());
        orders.setStatus(2);
        orders.setAmount(BigDecimal.valueOf(amount.get()));
        orders.setPhone(user.getPhone());
        orders.setNumber(String.valueOf(id));
        this.save(orders);

        //插入订单明细
        orderDetailService.saveBatch(orderDetails);

        //下单之后删除该id下的所有数据购物车
        LambdaQueryWrapper<ShoppingCart>  lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);

    }
}