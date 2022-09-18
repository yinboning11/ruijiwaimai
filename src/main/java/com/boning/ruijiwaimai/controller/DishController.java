package com.boning.ruijiwaimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boning.ruijiwaimai.common.R;
import com.boning.ruijiwaimai.dto.DishDto;
import com.boning.ruijiwaimai.entity.CategoryEntity;
import com.boning.ruijiwaimai.entity.DishEntity;
import com.boning.ruijiwaimai.entity.DishFlavorEntity;
import com.boning.ruijiwaimai.service.CategoryService;
import com.boning.ruijiwaimai.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import com.boning.ruijiwaimai.service.DishService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 菜品管理
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:34
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("接收的dishDto数据：{}", dishDto.toString());

        //保存数据到数据库
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<DishEntity> pageInfo = new Page<>();
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<DishEntity> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(name != null, DishEntity::getName, name);
        queryWrapper.orderByDesc(DishEntity::getUpdateTime);
        //执行分页查询
        Page<DishEntity> dishEntityPage = dishService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(dishEntityPage, dishDtoPage, "records");
        List<DishEntity> records = dishEntityPage.getRecords();
        List<DishDto> dishDtos = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            CategoryEntity categoryEntity = categoryService.getById(categoryId);
            DishDto dishDto1 = new DishDto();
            BeanUtils.copyProperties(item, dishDto1);
            dishDto1.setCategoryName(categoryEntity.getName());
            return dishDto1;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    /**
     * 修改套餐信息回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        //查询
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("接收的dishDto数据：{}", dishDto.toString());

        //保存数据到数据库
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("根据id删除菜品信息...");
        if (null == ids || ids.size() == 0) {
            return R.error("菜品id不能为空");
        }
        boolean b = dishService.deleteWhitFlavor(ids);
        // 如果菜品正在售卖中
        if (!b) {
            return R.error("菜品正在售卖中，不能删除");
        }
        return R.success("删除成功");
    }


    /**
     * 起售或者停售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {

        dishService.updateStatus(status, ids);
        return R.success("状态更改成功");
    }

    /**
     * 菜品模块回显
     *
     * @param dishEntity
     * @return
     */

    @GetMapping("/list")
    public R<List<DishDto>> list(DishEntity dishEntity) {

        List<DishDto> dishDtoList = null;
        // 使用缓存进行代码优化
        String key = "dish_" + dishEntity.getCategoryId() + "_" + dishEntity.getStatus();
        // 首先要从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        // 如果缓存中存在则直接返回
        if (null != dishDtoList) {
            return R.success(dishDtoList);
        }
        log.info("dish:{}", dishEntity);
        //条件构造器
        LambdaQueryWrapper<DishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(dishEntity.getName()), DishEntity::getName, dishEntity.getName());
        queryWrapper.eq(null != dishEntity.getCategoryId(), DishEntity::getCategoryId, dishEntity.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(DishEntity::getStatus, 1);
        queryWrapper.orderByDesc(DishEntity::getUpdateTime);
        // 列出所有的Dish
        List<DishEntity> dishes = dishService.list(queryWrapper);

        dishDtoList = dishes.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            CategoryEntity categoryEntity = categoryService.getById(item.getCategoryId());
            if (categoryEntity != null) {
                dishDto.setCategoryName(categoryEntity.getName());
            }
            LambdaQueryWrapper<DishFlavorEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavorEntity::getDishId, item.getId());

            dishDto.setFlavors(dishFlavorService.list(wrapper));
            return dishDto;
        }).collect(Collectors.toList());
        // 如果不存在，就查数据库，然后存到redis中 60分钟后过期
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

}
