package com.itcast.reggie.common;


import com.itcast.reggie.common.exception.CustomException;
import com.itcast.reggie.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@ResponseBody
@Slf4j
public class ProjectExceptionAdvice { //异常处理拦截

    @Autowired
    private MailService mailService;
    // 发件人要跟yml配置文件里填写的邮箱一致
    String mailFrom = "sky8923587@163.com";
    // 收件人
    String mailTo = "17509313978@163.com";
    // 抄送
    String cc = "sky8923587@163.com";

    @ExceptionHandler
   public R<String> ex(SQLIntegrityConstraintViolationException e){
       log.info(e.getMessage());
       if (e.getMessage().contains("Duplicate entry")){
           String[] split=e.getMessage().split(" ");
           //字符串截取
           String msg=split[2]+"已存在";
           return R.error(msg);
       }
       return R.error("未知错误");
   }


   //自定义异常

    @ExceptionHandler(CustomException.class)
    public R<String> ex(CustomException e){
        log.info(e.getMessage());
        mailService.sendSimpleMail(mailFrom, "Sky", mailTo, cc, "用户删除异常通知", e.getMessage());
        return R.error(e.getMessage());
    }


   @ExceptionHandler
    public R<String> ex(IOException e){
        log.info(e.getMessage());
        mailService.sendSimpleMail(mailFrom, "Sky", mailTo, cc, "文件上传异常", e.getMessage());
        return R.error("文件上传错误");
    }

}
