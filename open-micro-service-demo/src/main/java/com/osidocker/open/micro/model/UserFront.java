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
public class UserFront {

    private String name;
    private String folk;
    private String brithday;
    private String address;
    private String cardNo;
    private String sex;

    public UserFront init(JSONObject data) throws Exception{
        //{"姓名":{"words":"王迎峰","location":{"top":48,"left":136,"width":88,"height":32}},"民族":{"words":"汉","location":{"top":113,"left":274,"width":18,"height":23}},"住址":{"words":"安徽省淮南市大通区大通居仁村三区39-5-6室","location":{"top":222,"left":135,"width":286,"height":65}},"公民身份号码":{"words":"340402197303110018","location":{"top":361,"left":245,"width":358,"height":33}},"出生":{"words":"19730311","location":{"top":163,"left":137,"width":219,"height":27}},"性别":{"words":"男","location":{"top":109,"left":137,"width":19,"height":26}}}
        try {
            JSONObject json = data.getJSONObject("words_result");
            this.name = json.getJSONObject("姓名").getString("words");
            this.folk = json.getJSONObject("民族").getString("words");
            this.brithday = json.getJSONObject("出生").getString("words");
            this.address = json.getJSONObject("住址").getString("words");
            this.cardNo = json.getJSONObject("公民身份号码").getString("words");
            this.sex = json.getJSONObject("性别").getString("words");
        } catch (JSONException e) {
            throw new Exception("图片内容识别识别!");
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public String getFolk() {
        return folk;
    }

    public String getBrithday() {
        return brithday;
    }

    public String getAddress() {
        return address;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getSex() {
        return sex;
    }

    @Override
    public String toString() {
        return "UserFront{" +
                "name='" + name + '\'' +
                ", folk='" + folk + '\'' +
                ", brithday='" + brithday + '\'' +
                ", address='" + address + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
