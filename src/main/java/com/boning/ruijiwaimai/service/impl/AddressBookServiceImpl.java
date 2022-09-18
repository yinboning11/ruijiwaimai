package com.boning.ruijiwaimai.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.AddressBookDao;
import com.boning.ruijiwaimai.entity.AddressBookEntity;
import com.boning.ruijiwaimai.service.AddressBookService;


@Service("addressBookService")
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBookEntity> implements AddressBookService {



}