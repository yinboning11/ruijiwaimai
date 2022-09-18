package com.boning.ruijiwaimai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 套餐
 * 
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@Data
@TableName("setmeal")
public class SetmealEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Long id;
	/**
	 * 菜品分类id
	 */
	private Long categoryId;
	/**
	 * 套餐名称
	 */
	private String name;
	/**
	 * 套餐价格
	 */
	private BigDecimal price;
	/**
	 * 状态 0:停用 1:启用
	 */
	private Integer status;
	/**
	 * 编码
	 */
	private String code;
	/**
	 * 描述信息
	 */
	private String description;
	/**
	 * 图片
	 */
	private String image;
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
