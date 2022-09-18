package com.boning.ruijiwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.boning.ruijiwaimai.dto.DishDto;
import com.boning.ruijiwaimai.entity.DishEntity;

import java.util.List;
import java.util.Map;

/**
 * 菜品管理
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:34
 */
public interface DishService extends IService<DishEntity> {


    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    boolean deleteWhitFlavor(List<Long> ids);

    void updateStatus(Integer status, List<Long> ids);
}

