package com.boning.ruijiwaimai.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单明细表
 * 
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@Data
@TableName("order_detail")
public class OrderDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 图片
	 */
	private String image;
	/**
	 * 订单id
	 */
	private Long orderId;
	/**
	 * 菜品id
	 */
	private Long dishId;
	/**
	 * 套餐id
	 */
	private Long setmealId;
	/**
	 * 口味
	 */
	private String dishFlavor;
	/**
	 * 数量
	 */
	private Integer number;
	/**
	 * 金额
	 */
	private BigDecimal amount;

}
