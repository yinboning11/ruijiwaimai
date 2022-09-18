package com.boning.ruijiwaimai.dto;

import com.boning.ruijiwaimai.entity.DishEntity;
import com.boning.ruijiwaimai.entity.DishFlavorEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends DishEntity{
    private List<DishFlavorEntity> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
