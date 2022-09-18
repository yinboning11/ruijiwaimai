package com.boning.ruijiwaimai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 菜品口味关系表
 * 
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@Data
@TableName("dish_flavor")
public class DishFlavorEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 菜品
	 */
	private Long dishId;
	/**
	 * 口味名称
	 */
	private String name;
	/**
	 * 口味数据list
	 */
	private String value;
	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
	/**
	 * 创建人
	 */
	@TableField(fill = FieldFill.INSERT)
	private Long createUser;
	/**
	 * 修改人
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updateUser;
	/**
	 * 是否删除
	 */
	private Integer isDeleted;

}
