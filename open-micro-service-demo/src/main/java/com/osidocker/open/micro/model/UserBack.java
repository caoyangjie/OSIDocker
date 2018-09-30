/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 17:07 2018/9/26
 * @修改说明：
 * @修改日期： 17:07 2018/9/26
 * @版本号： V1.0.0
 */
public class UserBack {
    private String validate1;
    private String validate2;
    private String authority;

    public UserBack init(JSONObject data){
        //{"失效日期":{"words":"20190622","location":{"top":339,"left":352,"width":96,"height":19}},"签发机关":{"words":"济南市公安局长清分局","location":{"top":0,"left":0,"width":433,"height":316}},"签发日期":{"words":"20090522","location":{"top":337,"left":240,"width":96,"height":21}}}
        try {
            JSONObject json = data.getJSONObject("words_result");
            this.validate1 = json.getJSONObject("签发日期").getString("words");
            this.validate2 = json.getJSONObject("失效日期").getString("words");
            this.authority = json.getJSONObject("签发机关").getString("words");
        } catch (JSONException e) {
        }
        return this;
    }

    public String getValidate1() {
        return validate1;
    }

    public String getValidate2() {
        return validate2;
    }

    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return "UserBack{" +
                "validate1='" + validate1 + '\'' +
                ", validate2='" + validate2 + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
