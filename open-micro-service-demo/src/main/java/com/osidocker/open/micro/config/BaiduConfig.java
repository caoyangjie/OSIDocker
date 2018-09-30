/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.config;

import com.baidu.aip.ocr.AipOcr;
import com.osidocker.open.micro.model.UserBack;
import com.osidocker.open.micro.model.UserFront;
import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:00 2018/9/26
 * @修改说明：
 * @修改日期： 18:00 2018/9/26
 * @版本号： V1.0.0
 */
@Component(BaiduConfig.baiduConfig)
@ConfigurationProperties(prefix = "baidu.properties")
public class BaiduConfig {
    public static final String baiduConfig = "baiduConfig";
    public static final String FRONT = "front";
    public static final String BACK = "back";

    private String appId;
    private String apiKey;
    private String secretKey;
    private String imgPath;

    public UserFront getUserFront(String image){
       return new UserFront().init(getJsonFormBaiduApi(FRONT,image));
    }

    public UserBack getUserBack(String image){
        return new UserBack().init(getJsonFormBaiduApi(BACK,image));
    }

    private JSONObject getJsonFormBaiduApi(String idCardSide,String image){
        AipOcr client = new AipOcr(appId,apiKey,secretKey);
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        options.put("detect_risk", "false");
        return client.idcard(image, idCardSide, options);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
