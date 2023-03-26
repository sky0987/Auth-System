package com.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth.dto.AuthParamsDto;
import com.auth.dto.XcUserExt;
import com.auth.mapper.XcMenuMapper;
import com.auth.mapper.XcPermissionMapper;
import com.auth.mapper.XcUserMapper;
import com.auth.po.XcMenu;
import com.auth.po.XcUser;
import com.auth.service.AuthService;
import com.auth.service.PhoneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * 类型 对象名=JSON.parseObject(JSON字符串, 类型.class);
 * 或 List<类型> list=JSON.parseArray(JSON字符串,类型.class);
 * 将对象转换为JSON字符串：
 * String json=JSON.toJSONString(要转换的对象)
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 10:37
 */

@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    AuthService  authService;

    @Autowired
    XcMenuMapper xcMenuMapper;


    //传入的请求认证的参数就是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println(s);
        //将传入的json转成AuthParamsDto对象
        AuthParamsDto authParamsDto=null;
        try {
           authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
       } catch (Exception e){
          throw new RuntimeException("请求认证参数不符合要求");
       }
        //调用统一execute方法完成认证  查询数据库的信息 比对账号密码和验证码
        //判断到底是短信验证码登录还是手机密码登录
        String authType = authParamsDto.getAuthType();
        //获取service名称
        String beanName = authType+"_authservice";
        boolean ms_authservice = beanName.equals("sms_authservice");
        XcUserExt xcUserExt=null;
        if (beanName.equals("password_authservice")){
            AuthService authService = applicationContext.getBean(beanName, AuthService.class);
           xcUserExt = authService.execute(authParamsDto);
        }else if (beanName.equals("sms_authservice")){
            PhoneService phoneService = applicationContext.getBean(beanName, PhoneService.class);
            xcUserExt = phoneService.Pexecute(authParamsDto);
        }
        //封装xcUserExt用户信息为UserDetails
        //认证完成之后调用方法生成令牌和对比权限
        UserDetails userPrincipal = getUserPrincipal(xcUserExt);

        return userPrincipal;
    }

    /**
     *
     * @param
     * @return
     */
    //XcUserExt
    public UserDetails getUserPrincipal(XcUserExt xcUser){
        //密码不参与
        String password = xcUser.getPassword();
        xcUser.setPassword(null);
        //权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        //将查询出来的权限表示服放在一个集合
        List<String> list=new ArrayList<>();
        for (XcMenu xcMenu : xcMenus) {
            String code = xcMenu.getCode();
            list.add(code);
        }
        //允许数组类型的  所以要转成数组
        String[] strings = list.toArray(new String[0]);
        //String[] authorities=  {"p1"};
        //将用户信息转json  因为生成令牌需要json数据
        String userJson = JSON.toJSONString(xcUser);
        System.out.println(userJson);
        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(strings).build();
        System.out.println(userDetails.getAuthorities().toArray());
        return  userDetails;
    }


}
