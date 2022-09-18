package com.boning.ruijiwaimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.boning.ruijiwaimai.common.BaseContext;
import com.boning.ruijiwaimai.common.R;
import com.boning.ruijiwaimai.entity.AddressBookEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import com.boning.ruijiwaimai.service.AddressBookService;

import java.util.List;


/**
 * 地址管理
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:34
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBookEntity> getDefault() {
        LambdaQueryWrapper<AddressBookEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBookEntity::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBookEntity::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBookEntity addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBookEntity>> list(AddressBookEntity addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBookEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBookEntity::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBookEntity::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }


    @GetMapping("/{ids}")
    public R<AddressBookEntity> listById(@PathVariable Long ids) {
        AddressBookEntity byId = this.addressBookService.getById(ids);
        return R.success(byId);
    }

    /**
     * 根据id删除用户地址
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteAddressById(String ids) {
        log.info("id:{}", ids);
        // 根据id删除指定地址
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

    /**
     * 更新用户地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> updateAddress(@RequestBody AddressBookEntity addressBook) {
        log.info("addressBook:{}", addressBook);
        addressBookService.updateById(addressBook);
        return R.success("更新成功");
    }

    /**
     * 新增
     *
     * @return
     */
    @PostMapping
    @Transactional
    public R<AddressBookEntity> save(@RequestBody AddressBookEntity addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }


    /**
     * 设置默认地址
     */
    @PutMapping("default")
    @Transactional
    public R<AddressBookEntity> setDefault(@RequestBody AddressBookEntity addressBook) {
        log.info("addressBook:{}", addressBook);
        // 这里我觉得应该优化一下，先将原来的默认地址改为 0，因为默认地址只有一个
        LambdaUpdateWrapper<AddressBookEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBookEntity::getUserId, BaseContext.getCurrentId());
        wrapper.eq(AddressBookEntity::getIsDefault, 1);
        wrapper.set(AddressBookEntity::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ? and is_default = 1
        addressBookService.update(wrapper);

        // 然后在更新传来的新默认地址
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
}
