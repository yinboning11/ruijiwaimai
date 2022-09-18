package com.boning.ruijiwaimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boning.ruijiwaimai.common.BaseContext;
import com.boning.ruijiwaimai.common.R;
import com.boning.ruijiwaimai.dto.OrdersDto;
import com.boning.ruijiwaimai.entity.OrderDetailEntity;
import com.boning.ruijiwaimai.entity.OrdersEntity;
import com.boning.ruijiwaimai.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import com.boning.ruijiwaimai.service.OrdersService;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 订单表
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页展示订单详情（管理员端展示）
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> showPage(int page, int pageSize, Long number, String beginTime, String endTime) {
        log.info("分页展示订单详情（管理员端展示）");
        Page<OrdersEntity> ordersPage = new Page(page, pageSize);

        LambdaQueryWrapper<OrdersEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null, OrdersEntity::getNumber, number)
                .gt(StringUtils.isNotEmpty(beginTime), OrdersEntity::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), OrdersEntity::getOrderTime, endTime);

        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }

    /**
     * 更新订单状态
     *
     * @param ordersEntity
     * @return
     */
    @PutMapping
    public R<OrdersEntity> updateStatus(@RequestBody OrdersEntity ordersEntity) {
        Integer status = ordersEntity.getStatus();
        if (status != null) {
            ordersEntity.setStatus(3);
        }
        log.info("====================" + ordersEntity.getId());

        ordersService.updateById(ordersEntity);
        return R.success(ordersEntity);
    }

    /**
     * 支付
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody OrdersEntity ordersEntity) {
        String key = "shopping_" + BaseContext.getCurrentId();
        redisTemplate.delete(key);

        log.info("订单信息:" + ordersEntity.toString());

        ordersService.submit(ordersEntity);
        return R.success("已成功下单!");

    }

    /**
     * 分页展示订单详情
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        log.info("分页展示订单详情");
        //分页构造器对象
        Page<OrdersEntity> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> pageDto = new Page<>(page, pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<OrdersEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrdersEntity::getUserId, BaseContext.getCurrentId());
        //这里树直接把分页的全部结果查询出来，没有分页条件
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(OrdersEntity::getOrderTime);
        this.ordersService.page(pageInfo, queryWrapper);

        //通过OrderId查询对应的OrderDetail
//        LambdaQueryWrapper<OrderDetail> queryWrapper2 = new LambdaQueryWrapper<>();

        //对OrderDto进行需要的属性赋值
        List<OrdersEntity> records = pageInfo.getRecords();
        List<OrdersDto> orderDtoList = records.stream().map((item) -> {
            OrdersDto orderDto = new OrdersDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            List<OrderDetailEntity> orderDetailList = orderDetailService.getOrderDetailsByOrderId(orderId);
            BeanUtils.copyProperties(item, orderDto);
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        // 使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }

}
