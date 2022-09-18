package com.boning.ruijiwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boning.ruijiwaimai.common.R;
import com.boning.ruijiwaimai.entity.CategoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import com.boning.ruijiwaimai.service.CategoryService;

import java.util.List;


/**
 * 菜品及套餐分类
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:34
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     *
     * @param categoryEntity
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody CategoryEntity categoryEntity) {
        log.info("category:{}", categoryEntity);
        categoryService.save(categoryEntity);
        return R.success("新增分类成功");
    }

    /**
     * 分类管理分页
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        Page<CategoryEntity> categoryEntityPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(CategoryEntity::getSort);
        categoryService.page(categoryEntityPage, queryWrapper);
        return R.success(categoryEntityPage);
    }

    /**
     * 删除分类信息
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("将要删除的分类id:{}", id);

        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }
    /**
     * 修改分类信息
     * @param categoryEntity
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody CategoryEntity categoryEntity){
        log.info("修改分类信息为:{}",categoryEntity);
        categoryService.updateById(categoryEntity);
        return R.success("修改分类信息成功");
    }

    /**
     * 菜品分类list标签
     * @param categoryEntity
     * @return
     */
    @GetMapping("/list")
    public R<List<CategoryEntity>> list(CategoryEntity categoryEntity){
        //条件构造器
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(categoryEntity.getType() != null,CategoryEntity::getType,categoryEntity.getType());
        //添加排序条件
        queryWrapper.orderByAsc(CategoryEntity::getSort).orderByDesc(CategoryEntity::getUpdateTime);
        //查询数据
        List<CategoryEntity> list = categoryService.list(queryWrapper);
        //返回数据
        return R.success(list);

    }

}
