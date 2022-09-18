package com.boning.ruijiwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.boning.ruijiwaimai.entity.OrdersEntity;

import java.util.Map;

/**
 * 订单表
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
public interface OrdersService extends IService<OrdersEntity> {


    void submit(OrdersEntity ordersEntity);
}

