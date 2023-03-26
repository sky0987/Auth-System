package com.auth.service.impl;

import com.auth.dto.AuthParamsDto;
import com.auth.dto.XcUserExt;
import com.auth.mapper.XcUserMapper;
import com.auth.po.XcUser;
import com.auth.service.PhoneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("sms_authservice")
public class PhoneAuthServiceImpl  implements PhoneService {


@Autowired
private RedisTemplate  redisTemplate;
    @Autowired
    private XcUserMapper  xcUserMapper;
    @Override
    public XcUserExt Pexecute(AuthParamsDto authParamsDto) {
        //拿到输入的手机验证码
        String checkcode = authParamsDto.getCheckcode();
        //调用接口拿到发送的短信手机验证码
        String code = redisTemplate.opsForValue().get("86"+authParamsDto.getCellphone()).toString();
        if (!checkcode.equals(code)){
            throw new RuntimeException("请输入正确的验证码");
        }
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, authParamsDto.getCellphone()));

        if(user==null){
            throw new RuntimeException("账号不存在");
        }
        XcUserExt  userExt=new XcUserExt();
        BeanUtils.copyProperties(user,userExt);
        return   userExt;
    }
}
