/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.generate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.entity.Report;
import com.osidocker.open.micro.mapper.LsbMapper;
import com.osidocker.open.micro.model.TransactionInfo;
import com.osidocker.open.micro.model.ValidateInfo;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.service.AbsGenerateService;
import com.osidocker.open.micro.service.GenerateService;
import com.osidocker.open.micro.service.exceptions.PythonDataException;
import com.osidocker.open.micro.utils.FastPdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 15:02 2018/8/31
 * @修改说明：
 * @修改日期： 15:02 2018/8/31
 * @版本号： V1.0.0
 */
@Service(BankGenerateServiceImpl.BANK_GENERATE_SERVICE)
public class BankGenerateServiceImpl extends AbsGenerateService implements GenerateService<JSONObject,ApiResponse> {
    public static final String REPORT = "report";
    public static final String BANK_GENERATE_SERVICE = "bankGenerateService";

    @Value("${lsb.pdf.base.path}")
    private String pdfBasePath;

    @Resource
    private LsbMapper lsbMapper;

    @Override
    public ApiResponse execute(JSONObject jsonObject) throws PythonDataException {
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        JSONArray dataVal = Optional.ofNullable(jsonArray).orElseThrow(() -> PythonDataException.BANK_FLOW_RECORD_EXCEPTION);
        ValidateInfo info = getValidateInfoFormJson((JSONObject) dataVal.get(0));
        Report reportData = getReportData((JSONObject) dataVal.get(0));
        if( reportData.getTransList().isEmpty() ){
            throw PythonDataException.BANK_FLOW_RECORD_EXCEPTION.init(info);
        }
        try {
            String filePath = "";
            //TODO 调用服务生成pdf文件
            Future<String> pdfPath = getThreadPoolTaskExecutor().submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String basePath = pdfBasePath;
                    String fileName = info.getFlowNo().replaceAll("-","");
                    FastPdf.contractHandler(REPORT, basePath, fileName, "", reportData);
                    return fileName;
                }
            });
            filePath = pdfPath.get(120,TimeUnit.SECONDS);
            info.setPdfUrl(filePath);
            info.setMessage("获取数据信息成功!");
            info.setStatus("成功");
            Long validateId = lsbMapper.addValidateInfo(info);
            reportData.getTransList().stream().forEach(ti->{
                ti.setValidateId(info.getValidateId());
            });
            lsbMapper.insertTransactionInfoList(reportData.getTransList());
        } catch (InterruptedException e) {
            logger.error("生成PDF文件被中断",e);
            throw PythonDataException.BANK_INTERRUPT_FILE_EXCEPTION.init(info);
        } catch (ExecutionException e) {
            logger.error("生成PDF文件执行异常",e);
            throw PythonDataException.BANK_GENERATE_FILE_EXCEPTION.init(info);
        } catch (TimeoutException e) {
            logger.error("生成PDF文件执行超时",e);
            throw PythonDataException.BANK_GENERATE_TIME_OUT_EXCEPTION.init(info);
        }
        return ApiResponse.generator("000000","执行成功!");
    }

    private ValidateInfo getValidateInfoFormJson(JSONObject jsonObject) {
        String[] data = getFlowNos(jsonObject);
        ValidateInfo info = new ValidateInfo();
        info.setCustMbl(jsonObject.getString("account_mobile"));
        info.setAccountNo(jsonObject.getString("account_card_no"));
        info.setCreateDate(new Date());
        info.setCustName(jsonObject.getString("login_name"));
        info.setStatus("1");
        info.setRegistDate(jsonObject.getString("regist_date"));
        info.setBankName(jsonObject.getString("bankName"));
        info.setFlowNo(data[0]);
        info.setUserId(data[1]);
        return info;
    }

    private Report getReportData(JSONObject dat){
        Report report = new Report();
        report.setAccountNo(dat.getString("account_card_no"));
        report.setBankName(dat.getString("bankName"));
        report.setUserName(dat.getString("login_name"));
        report.setIdCard("******************");
        JSONArray transList = dat.getJSONArray("translist");
        List<TransactionInfo> transactionInfos = Stream.of(transList.toArray()).map(j->{
            TransactionInfo ti = new TransactionInfo(true);
            JSONObject data = (JSONObject) j;
            ti.setBalance(new BigDecimal(data.get("balance")+""));
            ti.setOtherAccount(data.get("other_acount")+"");
            ti.setOtherAccountName(data.get("other_acount_name")+"");
            ti.setTransAddress(data.get("trans_address")+"");
            ti.setTransMoney(Optional.ofNullable(data.get("income_money")).orElse("0")+"");
            if( "0".equalsIgnoreCase(ti.getTransMoney()) ){
                ti.setTransMoney("-"+Optional.ofNullable(data.get("pay_money")).orElse("0")+"");
            }
            ti.setTransRemark(data.get("trans_remark")+"");
            if( "".equalsIgnoreCase(ti.getTransRemark()) ){
                ti.setTransRemark(data.get("trans_desc")+"");
            }
            ti.setTransTime(data.get("trans_time")+"");
            ti.setTransCurrency(data.get("trans_currency")+"");
            ti.setTransType(data.get("trans_type")+"");
            return ti;
        }).sorted(new Comparator<TransactionInfo>() {
            @Override
            public int compare(TransactionInfo o1, TransactionInfo o2) {
                return o1.getTransTime().compareTo(o2.getTransTime());
            }
        }).collect(Collectors.toList());
        report.setTransList(transactionInfos);
        return report;
    }

}
