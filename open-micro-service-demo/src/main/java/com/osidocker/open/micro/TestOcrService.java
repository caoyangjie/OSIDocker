/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro;

import com.baidu.aip.ocr.AipOcr;
import com.osidocker.open.micro.model.UserBack;
import com.osidocker.open.micro.model.UserFront;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 16:34 2018/9/26
 * @修改说明：
 * @修改日期： 16:34 2018/9/26
 * @版本号： V1.0.0
 */
public class TestOcrService {
    public static void main(String[] args){
        AipOcr client = new AipOcr("14279554", "8ouRrPcriKFGzRWbjkIri0RO", "lQLF4e0PuOQclrumcjYloc23uGLSsdDr");
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        options.put("detect_risk", "false");

        String idCardSide = "front";

        // 参数为本地图片路径
        String image = "D://test//1.jpg";
        JSONObject res = client.idcard(image, idCardSide, options);
        System.out.println(new UserFront().init(res));
        //{"姓名":{"words":"王迎峰","location":{"top":48,"left":136,"width":88,"height":32}},"民族":{"words":"汉","location":{"top":113,"left":274,"width":18,"height":23}},"住址":{"words":"安徽省淮南市大通区大通居仁村三区39-5-6室","location":{"top":222,"left":135,"width":286,"height":65}},"公民身份号码":{"words":"340402197303110018","location":{"top":361,"left":245,"width":358,"height":33}},"出生":{"words":"19730311","location":{"top":163,"left":137,"width":219,"height":27}},"性别":{"words":"男","location":{"top":109,"left":137,"width":19,"height":26}}}
        idCardSide = "back";
        image = "D://test//2.jpg";
        res = client.idcard(image, idCardSide, options);
        System.out.println(new UserBack().init(res));
        //{"失效日期":{"words":"20190622","location":{"top":339,"left":352,"width":96,"height":19}},"签发机关":{"words":"济南市公安局长清分局","location":{"top":0,"left":0,"width":433,"height":316}},"签发日期":{"words":"20090522","location":{"top":337,"left":240,"width":96,"height":21}}}
    }
}
