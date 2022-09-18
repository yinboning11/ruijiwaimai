package com.boning.ruijiwaimai.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import com.boning.ruijiwaimai.service.SetmealDishService;




/**
 * 套餐菜品关系
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/setmealdish")
public class SetmealDishController {
    @Autowired
    private SetmealDishService setmealDishService;


}
