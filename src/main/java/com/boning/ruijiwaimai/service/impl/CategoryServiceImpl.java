package com.boning.ruijiwaimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boning.ruijiwaimai.common.CustomException;
import com.boning.ruijiwaimai.entity.DishEntity;
import com.boning.ruijiwaimai.entity.SetmealEntity;
import com.boning.ruijiwaimai.service.DishService;
import com.boning.ruijiwaimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.boning.ruijiwaimai.dao.CategoryDao;
import com.boning.ruijiwaimai.entity.CategoryEntity;
import com.boning.ruijiwaimai.service.CategoryService;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<DishEntity> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加dish查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(DishEntity::getCategoryId, id);
        long count1 = dishService.count(dishLambdaQueryWrapper);
        log.info("==========查询到关联菜品个数==========" + count1);

        if (count1 > 0) {
            //已关联菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<SetmealEntity> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(SetmealEntity::getCategoryId, id);
        //添加dish查询条件，根据分类id进行查询
        Long count2 = setmealService.count(setmealLambdaQueryWrapper);
        log.info("==========查询到关联套餐个数==========" + count2);

        //查看当前分类是否关联了套餐，如果已经关联，则抛出异常
        if (count2 > 0) {
            //已关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}