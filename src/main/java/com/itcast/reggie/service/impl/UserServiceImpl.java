package com.itcast.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;;
import com.itcast.reggie.entity.User;
import com.itcast.reggie.mapper.UserMapper;
import com.itcast.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
