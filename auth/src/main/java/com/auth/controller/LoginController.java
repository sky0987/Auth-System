package com.auth.controller;


import com.auth.config.R;
import com.auth.dto.AuthParamsDto;
import com.auth.dto.XcUserExt;
import com.auth.mapper.XcUserMapper;
import com.auth.po.XcUser;
import com.auth.util.SendSms;
import com.feignapi.client.CodeClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;


/**
 * PreAuthorize需要的权限标识符
 * security只要pom里面导入 立马需要认证
 */

@RestController
public class LoginController {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private CodeClient codeClient;


    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping("/login-success")
    public  R<String> loginSuccess() {
        return R.success("成功登录");
    }

    /**
     * 短信验证码发送
     * @params
     * @return
     */

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody XcUser  xcUser){
        String cellphone = "86"+xcUser.getCellphone();
        //生成验证码
        String pcode = codeClient.code(xcUser.getCellphone());
        SendSms.sendMsg(pcode,cellphone);
        //发送手机验证码之后存入redis中
        redisTemplate.opsForValue().set(cellphone,pcode,5l, TimeUnit.MINUTES);
        return R.success("发送成功");
    }







}
