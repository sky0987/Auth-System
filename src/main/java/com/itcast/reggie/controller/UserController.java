package com.itcast.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itcast.reggie.common.R;
import com.itcast.reggie.entity.User;
import com.itcast.reggie.service.UserService;
import com.itcast.reggie.utils.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

@Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
     public R<String> sendMsg(HttpSession session, @RequestBody User  user){
        String phone ="+86"+user.getPhone();
        System.out.println(phone);
        String code="";
        String ck="1234567890";
        Random r=new Random();
        for (int i = 0; i < 6; i++) {
            code+=ck.charAt(r.nextInt(ck.length()));
        }
        System.out.println(code);

        //SendSms.sendMsg(code,phone);

        //将生成的验证码保存到session中
        session.setAttribute("code",code);
        return  R.success("短信发送成功");
}

   @PostMapping("/login")
    public R<User> login(HttpSession session,@RequestBody Map<String,String> map){
       String phone = map.get("phone");
       String code = map.get("code");
       if (!code.equals(session.getAttribute("code"))){
          return R.error("验证码错误，请重新输入");
       }
       //判断用户是否存在
       LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(User::getPhone,phone);
       User user = userService.getOne(lambdaQueryWrapper);
       if (user==null){
           user=new User();
           user.setPhone(phone);
           userService.save(user);
       }
       session.setAttribute("user",user.getId());
       return R.success(user);
   }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        //清理Session中保存的当前登录员工的id
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
