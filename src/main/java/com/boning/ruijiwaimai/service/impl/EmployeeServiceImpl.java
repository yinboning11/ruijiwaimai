package com.boning.ruijiwaimai.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.boning.ruijiwaimai.dao.EmployeeDao;
import com.boning.ruijiwaimai.entity.EmployeeEntity;
import com.boning.ruijiwaimai.service.EmployeeService;


@Service("employeeService")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, EmployeeEntity> implements EmployeeService {



}