package com.itcast.reggie.controller;

import com.itcast.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;//dfsdfdfd.jpg

        System.out.println(basePath);

        //创建一个目录对象
        File dir = new File(basePath);//    src/main/resource/upload/
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //创建文件名   src/main/resources/upload/20cea521-4d89-44e8-a570-7669fc145b88.jpeg
        //File tempFile=new File(basePath+fileName);
       /* file.transferTo(tempFile);*/
       //将当前文件转存到指定目录
        //FileUtils.copyInputStreamToFile(file.getInputStream(),tempFile);
        IOUtils.copy(file.getInputStream(),new FileOutputStream(basePath+fileName));

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
   @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            //name来自于上传时返回的文件名称，输入流读出当前传入的名称，获取文件路径和名称
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
