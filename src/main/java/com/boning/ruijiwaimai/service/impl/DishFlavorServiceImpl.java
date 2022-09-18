package com.boning.ruijiwaimai.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.DishFlavorDao;
import com.boning.ruijiwaimai.entity.DishFlavorEntity;
import com.boning.ruijiwaimai.service.DishFlavorService;


@Service("dishFlavorService")
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavorEntity> implements DishFlavorService {


}