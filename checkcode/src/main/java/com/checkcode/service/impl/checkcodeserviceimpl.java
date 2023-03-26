package com.checkcode.service.impl;

import com.checkcode.service.checkcode;
import com.checkcode.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class checkcodeserviceimpl implements checkcode {

    private  static  String KEY="验证码"+ LocalDateTime.now() + UUID.randomUUID();
    //private  static  String PHONEKEY="手机验证码"+ LocalDateTime.now() + UUID.randomUUID();
  private   String [] path={"000000","00000","0000","000","00","0",""};

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Object verify(HttpServletResponse resp)  {
        try {
            ServletOutputStream out = resp.getOutputStream();
            Map<String,Object> map = ImageUtil.generateCodeAndPic();
            ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", out);
            //String key="code"+ LocalDateTime.now() +UUID.randomUUID();
            redisTemplate.opsForValue().set(KEY,map.get("code").toString(),5, TimeUnit.MINUTES);
            return  map.get("code");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean checkcode(String code) {
        Object code1 = redisTemplate.opsForValue().get(KEY);
        if (!code.equalsIgnoreCase(code1.toString())){
            return  false;
        }
        return  true;
    }

    @Override
    public String code(String tel) {
       /* String ck="1234567890";
        StringBuilder  str=new StringBuilder();
        Random  a=new Random();
        for (int i = 0; i < 6; i++) {
            str.append(ck.charAt(a.nextInt(ck.length())));
        }
        return  str.toString();*/
        int i = tel.hashCode();
        int ec=20206666;
        long re =Long.valueOf(i ^ ec);
        long  time=System.currentTimeMillis();
        re=re ^ time;
        long  code=re % 1000000;
        code= code<0?-code:code;
        String codeStr=code+"";
        int  len=codeStr.length();
        return  path[len]+codeStr;

    }

}
