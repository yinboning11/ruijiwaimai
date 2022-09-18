package com.boning.ruijiwaimai.controller;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import com.boning.ruijiwaimai.service.OrderDetailService;



/**
 * 订单明细表
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/orderdetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

}
