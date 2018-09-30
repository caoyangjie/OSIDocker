/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.controllers;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.config.BaiduConfig;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 17:40 2018/9/26
 * @修改说明：
 * @修改日期： 17:40 2018/9/26
 * @版本号： V1.0.0
 */
@RestController
@RequestMapping("/upload")
public class ImageUploadController {

    private Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private BaiduConfig baidu;

    public ApiResponse uploadImage(@RequestPart("file") MultipartFile file){
        if (file.isEmpty()) {
            return ApiResponse.generator("999999","上传图片不允许为空!");
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        logger.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        logger.info("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        String filePath = baidu.getImgPath();
        // 解决中文问题，liunx下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return ApiResponse.generator("000000","文件上传成功!").initData(filePath+fileName);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return ApiResponse.generator("999999","文件上传失败!");
    }

    @RequestMapping(value = "/{type}",method = RequestMethod.POST)
    public ApiResponse uploadImgAndGetInfo(@PathVariable("type") String type,@RequestPart("file") MultipartFile file){
        ApiResponse response = uploadImage(file);
        if( response.getApiCode().equalsIgnoreCase("000000") ){
            if( type.equalsIgnoreCase("front") ){
                return ApiResponse.generator("000000","识别成功!").initData(baidu.getUserFront(response.getRspVo()+""));
            }else if( type.equalsIgnoreCase("back") ){
                return ApiResponse.generator("000000","识别成功!").initData(baidu.getUserBack(response.getRspVo()+""));
            }else{
                return ApiResponse.generator("999999","不支持的类型!");
            }
        }
        return response;
    }
}
