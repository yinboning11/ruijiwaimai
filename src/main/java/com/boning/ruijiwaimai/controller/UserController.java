package com.boning.ruijiwaimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boning.ruijiwaimai.common.R;
import com.boning.ruijiwaimai.entity.UserEntity;
import com.boning.ruijiwaimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import com.boning.ruijiwaimai.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 用户信息
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送邮箱验证码
     *
     * @param userEntity
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody UserEntity userEntity, HttpSession session) {
        // 获取邮箱账号
        String email = userEntity.getEmail();
        String subject = "瑞吉餐购登录验证码";

        if (StringUtils.isNotEmpty(email)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用瑞吉餐购，登录验证码为: " + code + ",五分钟内有效，请妥善保管!";
            log.info("code={}", code);

            // 真正地发送邮箱验证码
            userService.sendMsg(email, subject, context);

            //  将随机生成的验证码保存到session中
//            session.setAttribute(phone, code);

            // 验证码由保存到session 优化为 缓存到Redis中，并且设置验证码的有效时间为 5分钟
            redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);

            return R.success("验证码发送成功，请及时查看!");
        }
        return R.error("验证码发送失败，请重新输入!");
    }


    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<UserEntity> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取手机号
//        String phone = map.get("phone").toString();

        String email = map.get("email").toString();

        //获取验证码
        String code = map.get("code").toString();


        // 从Redis中获取缓存验证码
        Object codeInSession = redisTemplate.opsForValue().get(email);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果能够比对成功，说明登录成功
            log.info("===========================登陆成功===============================");
            LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserEntity::getEmail, email);

            UserEntity userEntity = userService.getOne(queryWrapper);
            if (userEntity == null) {
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                UserEntity user = new UserEntity();
                user.setEmail(email);
                user.setStatus(1);
                // 取邮箱的前五位为用户名
                user.setName(email.substring(0, 6));
                userService.save(user);
            }
            session.setAttribute("user", userEntity.getId());

//            redisTemplate.opsForValue().set("userEmail",userEntity.getId());

            return R.success(userEntity);
        }
        return R.error("登录失败");
    }
    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("安全退出成功！");
    }
}
