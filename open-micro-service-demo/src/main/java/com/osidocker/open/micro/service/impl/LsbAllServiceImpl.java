/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service.impl;

import com.osidocker.open.micro.config.EmailConfig;
import com.osidocker.open.micro.entity.MyReportResponse;
import com.osidocker.open.micro.entity.Report;
import com.osidocker.open.micro.entity.SendReportResponse;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.mapper.LsbMapper;
import com.osidocker.open.micro.model.*;
import com.osidocker.open.micro.service.LsbAllService;
import com.osidocker.open.micro.utils.StringUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 16:53 2018/8/1
 * @修改说明：
 * @修改日期： 16:53 2018/8/1
 * @版本号： V1.0.0
 */
@Service("lsbAllService")
public class LsbAllServiceImpl implements LsbAllService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private EmailConfig config;

    @Resource
    private LsbMapper lsbMapper;

    @Override
    public void sendMessageMail(String userId,Long validateId, String title, String templateName, String recvMail) {
        try {
            Report report = getReportById(validateId);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,"UTF-8");
            //发送者
            helper.setFrom(config.getUsername());
            //发送给谁
            helper.setTo(recvMail);
            //邮件标题
            helper.setSubject("【" + title + "-" + LocalDate.now() + " " + LocalTime.now().withNano(0) + "】");

            try {
                Template template = configurer.getConfiguration().getTemplate(templateName);
                try {
                    String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, report);
                    helper.setText(text, true);
                    mailSender.send(mimeMessage);
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReceiveInfo info = new ReceiveInfo();
            info.setCreateDate(new Date());
            info.setUserId(userId);
            info.setReceiveMail(recvMail);
            info.setStatus("已发送");
            info.setValidateId(validateId);
            lsbMapper.insertReceiveInfo(info);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ShowUserEntity getUserInfo(String userId) {
        return lsbMapper.getUserInfo(userId);
    }

    private Report getReportById(Long validateId) {
        ValidateInfo validateInfo = lsbMapper.searchValidateInfo(validateId);
        List<TransactionInfo> transactionInfoList = lsbMapper.getTransactionInfoList(validateId);
        Report report = new Report();
        report.setIdCard("432522**********52");
        report.setUserName(validateInfo.getCustName());
        report.setBankName(validateInfo.getBankName());
        report.setAccountNo(validateInfo.getAccountNo());
        report.setTransList(transactionInfoList);
        return report;
    }

    @Override
    public List<MyReportResponse> searchUserReport(String userId) {
        return lsbMapper.getMyReportList(userId);
    }

    @Override
    public List<SendReportResponse> searchUserSendReport(String userId) {
        return lsbMapper.getReportSendMailList(userId);
    }

    @Override
    public MyReportResponse getMyLastReport(String userId) {
        return lsbMapper.getMyLastReportList(userId);
    }

    @Override
    public List<UserEducational> getUserEducationList(String userId) {
        return lsbMapper.getUserEducationalInfos(userId);
    }

    @Override
    public List<SupportOperation> getOperationList(String type) {
        return lsbMapper.searchOperationByType(type);
    }

    @Override
    public boolean updateUserInfo(ShowUserEntity user) {
        //判断是否为新用户
        if(StringUtil.isEmpty(user.getUserId())){
            //是新用户则执行注册登记
            user.setUserId(UUID.randomUUID().toString().replace("-", "").toLowerCase());
            user.setUnionId(user.getOpenId());
            user.setUnionType("wexin");
            lsbMapper.addSystemUser(user);
            lsbMapper.updateWexinUser(user);
        }else{
            //旧有用户更新信息
            lsbMapper.updateUserInfo(user);
        }
        return false;
    }
}
