package com.boning.ruijiwaimai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.boning.ruijiwaimai.entity.UserEntity;

import java.util.Map;

/**
 * 用户信息
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
public interface UserService extends IService<UserEntity> {


    void sendMsg(String to, String subject, String context);
}

