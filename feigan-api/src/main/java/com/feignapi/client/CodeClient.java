package com.feignapi.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * 1.feign调用请求方式的路径需要一致 需要在消费者的启动类加入启动客户端 需要将服务消费者和提供者加入nacos中注册服务才可以通过服务器访问
 * 2.如果需要post种添加参数  需要@RequestParam("code") 添加单个参数
 * 3.post请求单个参数的时候 地址为：http://localhost:8080/check/code?code="khkhj"
 * 4.get请求 http://localhost:8080/check/1
 */
@FeignClient(value = "checkcode",url = "http://localhost:8080")
public interface CodeClient {
    @PostMapping("/check/code")
   Boolean checkcode(@RequestParam("code") String code);

    @GetMapping( "/check/codep")
    String code(@RequestParam("tel") String tel);


}
