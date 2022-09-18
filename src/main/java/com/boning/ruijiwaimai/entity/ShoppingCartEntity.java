package com.boning.ruijiwaimai.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


/**
 * 购物车
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@Data
@TableName("shopping_cart")
public class ShoppingCartEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 图片
     */
    private String image;
    /**
     * 主键
     */
    private Long userId;
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
    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
