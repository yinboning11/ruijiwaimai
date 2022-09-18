package com.boning.ruijiwaimai.dao;

import com.boning.ruijiwaimai.entity.OrdersEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表
 * 
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@Mapper
public interface OrdersDao extends BaseMapper<OrdersEntity> {
	
}
