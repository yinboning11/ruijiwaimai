package com.boning.ruijiwaimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boning.ruijiwaimai.common.CustomException;
import com.boning.ruijiwaimai.dto.SetmealDto;
import com.boning.ruijiwaimai.entity.SetmealDishEntity;
import com.boning.ruijiwaimai.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.SetmealDao;
import com.boning.ruijiwaimai.entity.SetmealEntity;
import com.boning.ruijiwaimai.service.SetmealService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service("setmealService")
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, SetmealEntity> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        save(setmealDto);

        List<SetmealDishEntity> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where ids in(1,2,3) and status = 1
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<SetmealEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealEntity::getId, ids);
        queryWrapper.eq(SetmealEntity::getStatus, 1);

        Long count = super.count(queryWrapper);

        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据
        super.removeByIds(ids);

        //删除关系表中的数据
        //delete from setmeal_dish where setmeal_id in(1,2,3)
        LambdaQueryWrapper<SetmealDishEntity> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDishEntity::getSetmealId, ids);

        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        LambdaUpdateWrapper<SetmealEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SetmealEntity::getId, ids);
        // 设置状态
        updateWrapper.set(SetmealEntity::getStatus, status);
        this.update(updateWrapper);
    }


    /**
     * 根据id查询套餐信息和对应的菜品信息
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 先查出套餐表信息
        SetmealEntity setmeal = this.getById(id);
        // 但是套餐表缺少套餐菜品信息,所以需要借助 SetmealDto来操作
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDishEntity::getSetmealId, id);
        List<SetmealDishEntity> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }


}