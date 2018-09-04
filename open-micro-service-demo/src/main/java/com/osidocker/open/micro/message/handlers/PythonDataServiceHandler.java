/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.message.handlers;

import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.mapper.LsbMapper;
import com.osidocker.open.micro.message.AbsMessageHandler;
import com.osidocker.open.micro.message.dto.PythonDto;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.service.GenerateService;
import com.osidocker.open.micro.service.exceptions.PythonDataException;
import com.osidocker.open.micro.utils.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 9:07 2018/8/31
 * @修改说明：
 * @修改日期： 9:07 2018/8/31
 * @版本号： V1.0.0
 */
@Transactional
@Service(PythonDataServiceHandler.PYTHON_DATA_SERVICE_HANDLER)
public class PythonDataServiceHandler extends AbsMessageHandler<PythonDto> {

    public static final String PYTHON_DATA_SERVICE_HANDLER = "pythonData";
    public static final String SUCCESS_CODE = "000000";

    @Value("${lsb.python.service.resultUrl}")
    private String resultUrl;

    @Value("${lsb.python.service.ak}")
    private String pythonAK;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private LsbMapper lsbMapper;

    @Override
    public void execute(PythonDto message) {
        logger.info(JsonTools.toJson(message));
        if( SUCCESS_CODE.equalsIgnoreCase(message.getCode()) ){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flowNo",message.getFlowNo());
            jsonObject.put("ak", pythonAK);
            JSONObject result = restTemplate.postForObject(resultUrl,jsonObject,JSONObject.class);
            try {
                getGenerateService(message.getEventType()).execute(result);
            } catch (Exception e) {
                logger.error( "解析爬虫返回结果失败!", e );
                if( e instanceof PythonDataException){
                    lsbMapper.addValidateInfo(((PythonDataException) e).getValidateInfo());
                }
            }
        }
    }

    @Override
    public Class<PythonDto> messageClass() {
        return PythonDto.class;
    }
}

