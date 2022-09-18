package com.boning.ruijiwaimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.OrderDetailDao;
import com.boning.ruijiwaimai.entity.OrderDetailEntity;
import com.boning.ruijiwaimai.service.OrderDetailService;

import java.util.List;


@Service("orderDetailService")
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetailEntity> implements OrderDetailService {


    /**
     * 通过id获取用户的订单详情
     * @param orderId
     * @return
     */
    @Override
    public List<OrderDetailEntity> getOrderDetailsByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetailEntity::getOrderId, orderId);
        List<OrderDetailEntity> orderDetailList = this.list(queryWrapper);
        return orderDetailList;
    }
}