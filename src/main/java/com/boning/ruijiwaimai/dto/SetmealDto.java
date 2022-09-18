package com.boning.ruijiwaimai.dto;

import com.boning.ruijiwaimai.entity.SetmealDishEntity;
import com.boning.ruijiwaimai.entity.SetmealEntity;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends SetmealEntity {

    private List<SetmealDishEntity> setmealDishes;

    private String categoryName;
}
