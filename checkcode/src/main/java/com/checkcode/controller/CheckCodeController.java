package com.checkcode.controller;


import com.checkcode.service.checkcode;
import com.checkcode.util.ImageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.M
 * @version 1.0
 * @description 验证码服务接口
 * @date 2022/9/29 18:39
 */
@Api(value = "验证码服务接口")
@RestController
@RequestMapping("/check")
public class CheckCodeController {

    private  static  String KEY="验证码"+ LocalDateTime.now() +UUID.randomUUID();;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private checkcode checkcode;

    @ApiOperation(value="校验", notes="校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType="query")
    })
    @GetMapping("/pCode")
    public Object verify(HttpServletResponse resp) throws Exception{
      /*  ServletOutputStream out = resp.getOutputStream();
        Map<String,Object> map = ImageUtil.generateCodeAndPic();
        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", out);
        //String key="code"+ LocalDateTime.now() +UUID.randomUUID();
        redisTemplate.opsForValue().set(KEY,map.get("code").toString(),5, TimeUnit.MINUTES);
        return  map.get("code");*/
          return checkcode.verify(resp);
    }

    @PostMapping( "/code")
    public Boolean getCode (@RequestParam("code") String code){
           return   checkcode.checkcode(code);
    }


    /**
     * 获取自定义生成的手机验证码
     * @param
     * @return
     */
    @GetMapping( "/codep")
    public String pcode (@RequestParam("tel") String tel){
        return  checkcode.code(tel);
    }






}

