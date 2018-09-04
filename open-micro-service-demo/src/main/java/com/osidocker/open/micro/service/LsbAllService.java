/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.service;

import com.osidocker.open.micro.entity.MyReportResponse;
import com.osidocker.open.micro.entity.SendReportResponse;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.model.SupportOperation;
import com.osidocker.open.micro.model.UserEducational;

import java.util.List;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 16:51 2018/8/1
 * @修改说明：
 * @修改日期： 16:51 2018/8/1
 * @版本号： V1.0.0
 */
public interface LsbAllService {

    /**
     * 发送邮件
     * @param userId 用户Id
     * @param validateId 验证ID
     * @param title     邮件标题
     * @param templateName  模版名称
     * @param recvMail 接收者邮箱
     */
    void sendMessageMail(String userId, Long validateId, String title, String templateName, String recvMail);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    boolean updateUserInfo(ShowUserEntity user);

    /**
     * 根据用户Id获取用户基本信息
     * @param openId
     * @return
     */
    ShowUserEntity getUserInfo(String openId);

    /**
     * 查询用户报告
     * @param userId    用户ID
     * @return
     */
    List<MyReportResponse> searchUserReport(String userId);

    /**
     * 查询用户的发送报告记录
     * @param userId
     * @return
     */
    List<SendReportResponse> searchUserSendReport(String userId);

    /**
     * 获取用户最后一次认证报告
     * @param userId
     * @return
     */
    MyReportResponse getMyLastReport(String userId);

    /**
     * 根据用户Id获取用户列表
     * @param userId    用户Id
     * @return
     */
    List<UserEducational> getUserEducationList(String userId);

    /**
     * 根据操作类型获取可支持的爬取列表
     * @param type  爬取类型
     * @return
     */
    List<SupportOperation> getOperationList(String type);
}
