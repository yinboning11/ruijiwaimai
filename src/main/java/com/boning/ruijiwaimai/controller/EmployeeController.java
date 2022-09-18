package com.boning.ruijiwaimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boning.ruijiwaimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import com.boning.ruijiwaimai.entity.EmployeeEntity;
import com.boning.ruijiwaimai.service.EmployeeService;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 * 员工信息
 *
 * @author yinboning
 * @email 1096082464@qq.com
 * @date 2022-09-03 23:34:33
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employeeEntity
     * @return
     */
    //@RequestBody 主要用于接收前端传递给后端的json字符串（请求体中的数据）
    //HttpServletRequest request作用：如果登录成功，将员工对应的id存到session一份，这样想获取一份登录用户的信息就可以随时获取出来
    @PostMapping("/login")
    public R<EmployeeEntity> login(HttpServletRequest request, @RequestBody EmployeeEntity employeeEntity) {
        //1.将页面提交的密码进行MD5加密处理
        String password = employeeEntity.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<EmployeeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmployeeEntity::getUsername, employeeEntity.getUsername());
        EmployeeEntity emp = employeeService.getOne(queryWrapper);  //使用getOne：因为user_name字段有unique唯一约束，不会出现查询出多个结果

        //3.如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("账号不存在");  //因为error为静态方法，所以可以在该类中直接调用
        }

        //4.密码比对，如果不一致则返回登录结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码输入错误");
        }

        //5.查看员工状态，如果已为禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6.登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employeeEntity", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中保存的当前登录员工的id
        request.getSession().removeAttribute("employeeEntity");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @param request
     * @param employeeEntity
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody EmployeeEntity employeeEntity) {
        log.info("新增员工的信息：{}", employeeEntity.toString());
        //设置初始密码，需要进行md5加密
        employeeEntity.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//
//        employeeEntity.setCreateTime(LocalDateTime.now());
//        employeeEntity.setUpdateTime(LocalDateTime.now());

//        //强转为long类型
//        Long empID = (Long) request.getSession().getAttribute("employeeEntity");
//
//        employeeEntity.setCreateUser(empID);
//        employeeEntity.setUpdateUser(empID);

        employeeService.save(employeeEntity);
        return R.success("添加员工成功");
    }

    /**
     * 员工列表分页实现
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<EmployeeEntity> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), EmployeeEntity::getName, name);

        //添加排序添加
        queryWrapper.orderByDesc(EmployeeEntity::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 启用、禁用员工账号
     * @param request
     * @param employeeEntity
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody EmployeeEntity employeeEntity) {
        log.info(employeeEntity.toString());

//        Long empID = (Long) request.getSession().getAttribute("employeeEntity");
//        employeeEntity.setUpdateTime(LocalDateTime.now());
//        employeeEntity.setUpdateUser(empID);
        employeeService.updateById(employeeEntity);
        return R.success("员工修改信息成功");
    }

    /**
     * 修改员工信息
     */
    @GetMapping("/{id}")
    public R<EmployeeEntity> getById(@PathVariable Long id){

        log.info("根据id查询员工信息。。。");
        EmployeeEntity employeeEntity = employeeService.getById(id);
        if (employeeEntity != null){
            return R.success(employeeEntity);
        }
        return R.error("没有查询到该员工信息");
    }

}
