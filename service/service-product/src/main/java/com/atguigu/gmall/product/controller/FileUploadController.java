package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("admin/product")
public class FileUploadController {

    //文件上传的时候注意：文件服务器有可能会更改ip地址，就不能将ip写死在代码中，应该放在配置文件中：软编码

    @Value("${fileServer.url}")
    private String fileServerUrl;  //fileServerUrl=http://192.168.200.128:8080


    //文件上传
    //http://api.gmall.com/admin/product/fileUpload
    //Springmvc文件上传对象MultipartFile
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) throws IOException, MyException {
        /*
        文件上传
        1.读取tracker.conf   初始化
        2.创建一个trackerClient
        3.创建一个trackerServer
        4.创建一个storageClient
        5.上传
        6.将上传之后的文件url放入Result
         */
        String configFile = this.getClass().getResource("/tracker.conf").getFile();
        String path = null;
        //判断
        if(configFile != null){
            //初始化
            ClientGlobal.init(configFile);
            //创建一个trackerClient
            TrackerClient trackerClient = new TrackerClient();
            //创建一个trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //创建一个storageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,null);
            //上传
            //第一个参数表示上传文件的字节数组,第二个参数 后缀名（不变）
            String filename = file.getOriginalFilename();
            String extname = FilenameUtils.getExtension(filename);
            //
            path = storageClient1.upload_appender_file1(file.getBytes(), extname, null);

        }
        System.out.println(fileServerUrl+path);

        //返回数据
        return Result.ok(fileServerUrl+path);
    }

}
