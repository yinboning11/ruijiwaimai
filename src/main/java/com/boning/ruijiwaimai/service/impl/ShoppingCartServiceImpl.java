package com.boning.ruijiwaimai.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.ShoppingCartDao;
import com.boning.ruijiwaimai.entity.ShoppingCartEntity;
import com.boning.ruijiwaimai.service.ShoppingCartService;


@Service("shoppingCartService")
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCartEntity> implements ShoppingCartService {


}