package com.boning.ruijiwaimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boning.ruijiwaimai.dto.DishDto;
import com.boning.ruijiwaimai.entity.DishFlavorEntity;
import com.boning.ruijiwaimai.service.DishFlavorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.DishDao;
import com.boning.ruijiwaimai.entity.DishEntity;
import com.boning.ruijiwaimai.service.DishService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service("dishService")
@Transactional
public class DishServiceImpl extends ServiceImpl<DishDao, DishEntity> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表
        super.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //获取菜品口味
        List<DishFlavorEntity> flavors = dishDto.getFlavors();

        //将每条flavor的dishId赋上值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        DishEntity dishEntity = super.getById(id);
        BeanUtils.copyProperties(dishEntity, dishDto);
        LambdaQueryWrapper<DishFlavorEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavorEntity::getDishId, dishDto.getId());
        List<DishFlavorEntity> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);


        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        super.updateById(dishDto);
        //通过dish_id,删除菜品的flavor
        LambdaQueryWrapper<DishFlavorEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavorEntity::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //获取前端提交的flavor数据
        List<DishFlavorEntity> flavors = dishDto.getFlavors();

        //将每条flavor的dishId赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());


        //将数据批量保存到dish_flavor数据库
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    @Transactional
    public boolean deleteWhitFlavor(List<Long> ids) {
        //select count(*) from dish where id in (1,2,3) and status = 1
        //查询菜品状态，确定是否可用删除
        LambdaQueryWrapper<DishEntity> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(DishEntity::getId, ids);
        queryWrapper.eq(DishEntity::getStatus, 1);

        // 查看符合条件的记录数
        long count = this.count(queryWrapper);
        if (count > 0) {
            return false;
        }

        //如果可以删除，先删除菜品表中的数据---dish
        this.removeByIds(ids);

        //delete from dish_flavor where dish_id in (1,2,3)
        LambdaQueryWrapper<DishFlavorEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavorEntity::getDishId, ids);
        //删除关系表中的数据----dish_flavor
        dishFlavorService.remove(lambdaQueryWrapper);
        return true;
    }

    /**
     * 起售或者停售
     *
     * @param status
     * @param ids
     */
    @Override
    @Transactional
    public void updateStatus(Integer status, List<Long> ids) {
        // 先查询出目标菜品（简单优化后的）
        LambdaUpdateWrapper<DishEntity> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.in(DishEntity::getId, ids);
        // 设置状态
        updateWrapper.set(DishEntity::getStatus, status);
        this.update(updateWrapper);
    }

}