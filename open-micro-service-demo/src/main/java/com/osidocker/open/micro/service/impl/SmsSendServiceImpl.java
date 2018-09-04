/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.impl;

import clojure.lang.Obj;
import com.osidocker.open.micro.security.service.UserService;
import com.osidocker.open.micro.service.SmsSendService;
import com.osidocker.open.micro.service.exceptions.SmsSendException;
import com.osidocker.open.micro.utils.StringUtil;
import com.osidocker.open.micro.vo.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:29 2018/7/25
 * @修改说明：
 * @修改日期： 18:29 2018/7/25
 * @版本号： V1.0.0
 */
@Service
public class SmsSendServiceImpl implements SmsSendService<String,Response> {
    protected Logger logger = LoggerFactory.getLogger(SmsSendServiceImpl.class);
    public static final String LOGIN_PHONE_CODE = "loginPhoneCode:";
    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    public static final Pattern PHONE_NO_PATTERN = Pattern.compile("\\d{11}");
    static final char[] charStrs = new char[]{'0','1','2','3','4','5','6','7','8','9'};

    /**
     *  发送短信验证码服务实现
     * @param telephone
     * @return
     * @throws SmsSendException
     */
    @Override
    public Response sendMessage(String telephone) throws SmsSendException {
        try{
            // 判断是否已经存在,不存在在注册
            registerUserNotExist(telephone);
            //TODO 生成短信验证码并发送
            vaildAndSendCode(telephone);
        }catch(Exception e){
            throw new SmsSendException(-1,"短信验证码发送失败："+e.getMessage());
        }
        return new Response("000000","SUCCESS_CODE");
    }

    /**
     * 校验并发送短信验证码
     * @param telephone
     */
    private void vaildAndSendCode(String telephone) {
        if( telephone == null || !PHONE_NO_PATTERN.matcher(telephone).matches() ){
            throw new SmsSendException(-1,"手机号码格式不正确!");
        }
        String cacheKey = LOGIN_PHONE_CODE + telephone;
        String code = (String) redisTemplate.opsForValue().get(cacheKey);

        if( StringUtil.isEmpty(code) ){
            code = randomNumberString(6);
        }
        if( !sendLoginCode(telephone, code) ){
            throw new SmsSendException(-1,"短信验证码发送失败");
        }
        redisTemplate.opsForValue().set(cacheKey,code,10,TimeUnit.MINUTES);
    }

    /**
     * 发送短信验证码的函数代码
     * @param telephone 电话号码
     * @param code       验证码
     * @return  是否发送成功
     */
    private boolean sendLoginCode(String telephone, String code) {
        //TODO 待完成短信发送代码
        logger.info("发送短信验证码["+code+"]到手机号码["+telephone+"]");
        return true;
    }

    /**
     * 根据手机号码注册用户
     * @param telephone 电话号码
     */
    private void registerUserNotExist(String telephone) {
        if( userService.findUserByTelephone(telephone)==null ){
            if( !userService.registerUser(telephone) ){
                throw new SmsSendException(-1,"注册新用户失败!");
            }
        }
    }

    /**
     * 获取一定长度的随机数字字符串。
     *
     * @param length 指定字符串长度。
     * @return 一定长度的数字字符串。
     */
    public static String randomNumberString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(new SecureRandom().nextInt(10));
        }
        return stringBuilder.toString();
    }
}
