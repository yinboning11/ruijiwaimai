package com.boning.ruijiwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boning.ruijiwaimai.common.R;
import com.boning.ruijiwaimai.dto.DishDto;
import com.boning.ruijiwaimai.dto.SetmealDto;
import com.boning.ruijiwaimai.entity.CategoryEntity;
import com.boning.ruijiwaimai.entity.DishEntity;
import com.boning.ruijiwaimai.entity.SetmealDishEntity;
import com.boning.ruijiwaimai.entity.SetmealEntity;
import com.boning.ruijiwaimai.service.CategoryService;
import com.boning.ruijiwaimai.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import com.boning.ruijiwaimai.service.SetmealService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 套餐
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("接收的dishDto数据：{}", setmealDto.toString());

        //保存数据到数据库
        setmealService.saveWithDish(setmealDto);
        return R.success("新增菜品成功");
    }

    /**
     * 套餐分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<SetmealEntity> pageInfo = new Page<>();
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<SetmealEntity> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(name != null, SetmealEntity::getName, name);
        queryWrapper.orderByDesc(SetmealEntity::getUpdateTime);
        //执行分页查询
        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<SetmealEntity> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            CategoryEntity categoryEntity = categoryService.getById(categoryId);
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(categoryEntity.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);

        return R.success(setmealDtoPage);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids为：", ids);
        if (null == ids || ids.size() == 0) {
            return R.error("请选择删除对象");
        }
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");

    }

    /**
     * 起售或者停售（管理员使用）
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("根据id进行起售或者停售套餐……");
        if (null == ids || ids.size() == 0) {
            return R.error("套餐id不能为空");
        }
        setmealService.updateStatus(status, ids);
        return R.success("更改成功");
    }


    @GetMapping("/list")
    public R<List<SetmealEntity>> list(SetmealEntity setmealEntity) {

        List<SetmealEntity> list = null;
        //动态构造key
        String key = "setmeal_" + setmealEntity.getCategoryId() + "_" + setmealEntity.getStatus();   //setmeal_1524731277968793602_1
        //先从redis中获取数据;
        list = (List<SetmealEntity>) redisTemplate.opsForValue().get(key);

        if (list != null) {
            //如果存在，直接返回，无需查询数据库
            return R.success(list);
        }

        LambdaQueryWrapper<SetmealEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmealEntity.getCategoryId() != null, SetmealEntity::getCategoryId, setmealEntity.getCategoryId());
        queryWrapper.eq(setmealEntity.getStatus() != null, SetmealEntity::getStatus, setmealEntity.getStatus());
        queryWrapper.orderByDesc(SetmealEntity::getUpdateTime);

        list = setmealService.list(queryWrapper);
        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);

        return R.success(list);
    }


    @GetMapping("/dish/{ids}")
    public R<List<SetmealDishEntity>> dishlist(Long ids) {
        LambdaQueryWrapper<SetmealDishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDishEntity::getSetmealId, ids);
        List<SetmealDishEntity> list = setmealDishService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 根据id查询套餐信息和对应的菜品信息（管理员使用）
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {

        SetmealDto dishDto = setmealService.getByIdWithDish(id);

        return R.success(dishDto);
    }
}
