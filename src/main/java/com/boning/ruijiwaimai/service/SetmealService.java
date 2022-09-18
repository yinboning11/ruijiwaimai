package com.boning.ruijiwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.boning.ruijiwaimai.dto.SetmealDto;
import com.boning.ruijiwaimai.entity.SetmealEntity;

import java.util.List;
import java.util.Map;

/**
 * 套餐
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
public interface SetmealService extends IService<SetmealEntity> {


    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    void updateStatus(Integer status, List<Long> ids);

    SetmealDto getByIdWithDish(Long id);
}

