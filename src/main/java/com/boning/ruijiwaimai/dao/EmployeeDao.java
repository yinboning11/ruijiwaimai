package com.boning.ruijiwaimai.dao;

import com.boning.ruijiwaimai.entity.EmployeeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工信息
 * 
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@Mapper
public interface EmployeeDao extends BaseMapper<EmployeeEntity> {
	
}
