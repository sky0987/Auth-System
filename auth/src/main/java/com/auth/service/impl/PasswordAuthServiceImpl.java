package com.auth.service.impl;

import com.auth.dto.AuthParamsDto;
import com.auth.dto.XcUserExt;
import com.auth.mapper.XcUserMapper;
import com.auth.po.XcUser;
import com.auth.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feignapi.client.CodeClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Mr.M
 * @version 1.0
 * @description 账号名密码方式
 * @date 2023/2/24 11:56
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

 @Autowired
 XcUserMapper xcUserMapper;

 @Autowired
 PasswordEncoder passwordEncoder;

@Autowired
CodeClient codeClient;

 @Autowired
 private RedisTemplate redisTemplate;

 @Override
 public XcUserExt execute(AuthParamsDto authParamsDto) {
  //账号   authParamsDto这个输入的信息
  String username = authParamsDto.getUsername();

   if (authParamsDto.getCheckcode()==null ) {
    throw new RuntimeException("请输入验证码");
  }
  String checkcode1 = authParamsDto.getCheckcode();
  Boolean checkcode2 = codeClient.checkcode(checkcode1);
  //调用code的getcode方法  去判断输入的验证码和生成的验证码的正确
  if (checkcode2== null || !checkcode2 ){
   throw new RuntimeException("请输入正确的验证码");
  }
  //账号是否存在
  //根据username账号查询数据库
  XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));

  //查询到用户不存在，要返回null即可，spring security框架抛出异常用户不存在
  if(xcUser==null){
   throw new RuntimeException("账号不存在");
  }

  //验证密码是否正确
  //如果查到了用户拿到正确的密码
  String passwordDb = xcUser.getPassword();
  //拿 到用户输入的密码
  String passwordForm = authParamsDto.getPassword();
  //校验密码
  boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
  if(!matches){
   throw new RuntimeException("账号或密码错误");
  }
  XcUserExt xcUserExt = new XcUserExt();
  BeanUtils.copyProperties(xcUser,xcUserExt);

  return xcUserExt;
 }
}


