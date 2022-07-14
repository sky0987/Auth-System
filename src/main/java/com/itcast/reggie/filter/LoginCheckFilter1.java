package com.itcast.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itcast.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
/*
@WebFilter(filterName = "loginCheckFilter1",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter1 implements Filter{
    //路径匹配器，支持通配符
   public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求
        String requestURI = request.getRequestURI();// /backend/index.html
        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径
       String[] urls = new String[]{"/employee/login","/backend/page/login/**","/backend/js/**",
               "/backend/images/**","/backend/styles/**",
               "/backend/plugins/**","/backend/api/**","/employee/logout"};
        for (String url : urls) {
            //不能用contains 不是具体路径
            //判断释放的路径里面是否包含请求的路径，若果包含，则放行
           if (PATH_MATCHER.match(url,requestURI)==true){
               log.info("本次请求{}不需要处理",requestURI);
               filterChain.doFilter(request,response);
               return;
           }
        }


*/
/**
         * 如果访问的包含放行的资源，那么直接结束掉当前方法，不需要执行
         * 如果访问的不包含，那么方法不会终止，会执行下面的方法
         * 如果下面的方法有登录  那么也可以直接结束掉 不执行后面的方法
         * 如果木有登录，那么方法不会终止，会执行下面的方法
         *
         *1、如果访问的路径不包含放行的资源，那么相当于else 会只执行下面的方法
         *2、如果当前登录的账户为空，那么会执行else里面的方法
         *//*



        //判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            filterChain.doFilter(request,response);
        }else {
           // response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            //System.out.println(request.getContextPath());
            response.sendRedirect(request.getContextPath()+"/backend/page/login/login.html");
        }

    }

}

*/
