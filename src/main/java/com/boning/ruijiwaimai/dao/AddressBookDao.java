package com.boning.ruijiwaimai.dao;

import com.boning.ruijiwaimai.entity.AddressBookEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址管理
 * 
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:34
 */
@Mapper
public interface AddressBookDao extends BaseMapper<AddressBookEntity> {
	
}
