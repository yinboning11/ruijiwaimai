package com.boning.ruijiwaimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boning.ruijiwaimai.common.BaseContext;
import com.boning.ruijiwaimai.common.R;

import com.boning.ruijiwaimai.entity.ShoppingCartEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import com.boning.ruijiwaimai.service.ShoppingCartService;

import java.time.LocalDateTime;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 购物车
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    public R<ShoppingCartEntity> addToCart(@RequestBody ShoppingCartEntity shoppingCart) {
        // 清除缓存
        cleanCache();
        log.info("============在购物车中添加订单==============");
        log.info("购物车中的数据:{}" + shoppingCart.toString());

        //设置用户id,指定当前是哪个用户的 购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 查询当前菜品或套餐是否 在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCartEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据
        queryWrapper.eq(ShoppingCartEntity::getUserId, userId);

        // 先判断添加进购物车的是菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCartEntity::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCartEntity::getSetmealId, shoppingCart.getSetmealId());
        }


        ShoppingCartEntity oneCart = shoppingCartService.getOne(queryWrapper);
        //  如果购物车中 已经存在该菜品或套餐，其数量+1，不存在，就将该购物车数据保存到数据库中
        if (oneCart != null) {
            Integer number = oneCart.getNumber();
            oneCart.setNumber(number + 1);
            shoppingCartService.updateById(oneCart);
        } else {
            shoppingCart.setNumber(1);
            // 设置创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            oneCart = shoppingCart;
        }
        return R.success(oneCart);
    }

    // 在购物车中删减订单
    @PostMapping("/sub")
    public R<String> subToCart(@RequestBody ShoppingCartEntity shoppingCart) {
        // 清除缓存
        cleanCache();
        log.info("============在购物车中删减订单==============");

        log.info("购物车中的数据:{}" + shoppingCart.toString());

        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询当前菜品或套餐是否 在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCartEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据
        queryWrapper.eq(ShoppingCartEntity::getUserId, BaseContext.getCurrentId());

        // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCartEntity::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCartEntity::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCartEntity oneCart = shoppingCartService.getOne(queryWrapper);
        //  如果购物车中 已经存在该菜品或套餐
        if (oneCart != null) {
            Integer number = oneCart.getNumber();
            // 如果数量大于 0，其数量 -1， 否则清除
            if (number > 1) {
                oneCart.setNumber(number - 1);
                shoppingCartService.updateById(oneCart);
            } else {
                shoppingCartService.remove(queryWrapper);
            }
        }

        return R.success("成功删减订单!");
    }

    @GetMapping("/list")
    public R<List<ShoppingCartEntity>> list() {
        log.info("============list==============");

        // 缓存的购物车key
        String key = "shopping_" + BaseContext.getCurrentId();
        // 先查询redis中是否存在
        List<ShoppingCartEntity> shoppingCarts = (List<ShoppingCartEntity>) redisTemplate.opsForValue().get(key);
        // 如果缓存中无数据，那么就查询数据库
        if (null == shoppingCarts) {
            LambdaQueryWrapper<ShoppingCartEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCartEntity::getUserId, BaseContext.getCurrentId());

            // 最晚下单的 菜品或套餐在购物车中最先展示
            queryWrapper.orderByDesc(ShoppingCartEntity::getCreateTime);
            shoppingCarts = shoppingCartService.list(queryWrapper);

            // 存储到redis中
            redisTemplate.opsForValue().set(key, shoppingCarts, 60, TimeUnit.MINUTES);

        }
        return R.success(shoppingCarts);
    }

    @DeleteMapping("/clean")
    public R<String> cleanCart() {
        // 清除缓存
        cleanCache();
        LambdaQueryWrapper<ShoppingCartEntity> queryWrapper = new LambdaQueryWrapper<>();
        // DELETE FROM shopping_cart WHERE (user_id = ?)
        queryWrapper.eq(ShoppingCartEntity::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("成功清空购物车！");
    }

    /**
     * 清除redis缓存
     */
    public void cleanCache() {
        String key = "shopping_" + BaseContext.getCurrentId();
        redisTemplate.delete(key);
        log.info("============清楚redis缓存==============");
    }
}
