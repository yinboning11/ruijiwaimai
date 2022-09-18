package com.boning.ruijiwaimai.filter;


import com.alibaba.fastjson.JSON;
import com.boning.ruijiwaimai.common.BaseContext;
import com.boning.ruijiwaimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到的请求：{}", requestURI);
        //定义不需要处理的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求：{}，不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4.判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employeeEntity") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employeeEntity"));

            Long empId = (Long) request.getSession().getAttribute("employeeEntity");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2、判断 移动端(消费者端)登录状态(session中含有employee的登录信息)，如果已经登录，则直接放行
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null) {
            log.info("用户已经登录，用户id为:{}", userId);
            // 自定义元数据对象处理器 MyMetaObjectHandler中需要使用 登录用户id
            //   通过ThreadLocal set和get用户id
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //5.如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                //匹配
                return true;
            }
        }
        //不匹配
        return false;
    }
}
