package com.boning.ruijiwaimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.boning.ruijiwaimai.common.BaseContext;
import com.boning.ruijiwaimai.common.CustomException;
import com.boning.ruijiwaimai.entity.*;
import com.boning.ruijiwaimai.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.OrdersDao;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service("ordersService")
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, OrdersEntity> implements OrdersService {


    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    // 用户下单
    @Override
    @Transactional  // 需要操作多张表，涉及到事务
    //  向订单表中 插入数据 并 完善订单明细表，清空当前用户在 购物车 表 中的数据
    public void submit(OrdersEntity ordersEntity) {

        // 获取当前用户的id
        Long userId = BaseContext.getCurrentId();

        // 查询当前用户的 购物车数据
        LambdaQueryWrapper<ShoppingCartEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCartEntity::getUserId, userId);

        // 一个用户可能下单了 多个菜品或套餐，故应该查询到的是一个 购物车类型的list
        List<ShoppingCartEntity> cartList = shoppingCartService.list(queryWrapper);

        if (cartList == null || cartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单！");
        }

        // 查询用户数据
        UserEntity user = userService.getById(userId);

        // 查询用户的派送地址信息
        AddressBookEntity addressBook = addressBookService.getById(ordersEntity.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("您的地址信息有误，暂不能下单!");
        }


        long orderId = IdWorker.getId();  // 订单号

        //  购物车中 商品 的总金额 需要保证在多线程的情况下 也是能计算正确的，故需要使用原子类
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetailEntity> orderDetails = cartList.stream().map((item) -> {
            OrderDetailEntity orderDetail = new OrderDetailEntity();

            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            // 设置用户名
            orderDetail.setName(user.getName());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            // 总金额 = 单价 * 份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;

        }).collect(Collectors.toList());

        ordersEntity.setId(orderId);
        ordersEntity.setOrderTime(LocalDateTime.now());
        ordersEntity.setCheckoutTime(LocalDateTime.now());
        ordersEntity.setStatus(2);
        ordersEntity.setAmount(new BigDecimal(amount.get()));//总金额，需要 遍历购物车，计算相关金额来得到
        ordersEntity.setUserId(userId);
        ordersEntity.setNumber(String.valueOf(orderId));
        ordersEntity.setUserName(user.getName());
        ordersEntity.setConsignee(addressBook.getConsignee());
        ordersEntity.setPhone(addressBook.getPhone());
        ordersEntity.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        // 向订单表插入数据,一条数据，插入数据之前，需要填充如上属性
        this.save(ordersEntity);    //  --> ordersService.save(orders);

        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);

    }
}