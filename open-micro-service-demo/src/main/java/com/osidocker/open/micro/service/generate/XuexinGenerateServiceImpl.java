/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.generate;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.mapper.LsbMapper;
import com.osidocker.open.micro.model.UserEducational;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.service.AbsGenerateService;
import com.osidocker.open.micro.service.GenerateService;
import com.osidocker.open.micro.service.exceptions.PythonDataException;
import com.osidocker.open.micro.utils.Base64Util;
import com.osidocker.open.micro.utils.JsonTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 14:27 2018/9/3
 * @修改说明：
 * @修改日期： 14:27 2018/9/3
 * @版本号： V1.0.0
 */
@Service(XuexinGenerateServiceImpl.XUEXIN_GENERATE_SERVICE)
public class XuexinGenerateServiceImpl extends AbsGenerateService implements GenerateService<JSONObject,ApiResponse> {
    public static final String JPG = ".jpg";
    public static final String XUEXIN_GENERATE_SERVICE = "xuexinGenerateService";

    @Resource
    private LsbMapper lsbMapper;

    @Value("${lsb.img.basePath}")
    private String imgBasePath;

    @Override
    public ApiResponse execute(JSONObject jsonObject) throws PythonDataException {
        String[] flows = getFlowNos(jsonObject);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        JSONObject dataVal = Optional.ofNullable(jsonData).orElseThrow(() -> PythonDataException.XUEXIN_RECORD_EXCEPTION);
        logger.info(JsonTools.toJson(dataVal));
        UserEducational userEducational = JsonTools.fromJson(JsonTools.toJson(dataVal),UserEducational.class);
        userEducational.setFlowNo(flows[0]);
        userEducational.setUserId(flows[1]);
        generateImage(userEducational);
        lsbMapper.insertUserEducational(userEducational);
        return ApiResponse.generator("000000","学信信息记录成功!");
    }

    /**
     * 将头像base64码转化为图片
     * @param userEducational
     */
    private void generateImage(UserEducational userEducational){
        Date date = new Date();
        String datePath = new SimpleDateFormat("yyyy/MM/dd/").format(date);
        String path=imgBasePath+datePath;
        //如果不存在,创建文件夹
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        userEducational.setPhotoName(datePath+ userEducational.getFlowNo()+JPG);
        Base64Util.GenerateImage(userEducational.getPhoto(),imgBasePath,userEducational.getPhotoName());
    }
}
