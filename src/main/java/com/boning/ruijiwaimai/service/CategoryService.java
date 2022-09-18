package com.boning.ruijiwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boning.ruijiwaimai.entity.CategoryEntity;

import java.util.Map;

/**
 * 菜品及套餐分类
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:34
 */
public interface CategoryService extends IService<CategoryEntity> {

    void remove(Long id);
}

