/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.entity.MyReportResponse;
import com.osidocker.open.micro.entity.SendReportResponse;
import com.osidocker.open.micro.entity.ShowUserEntity;
import com.osidocker.open.micro.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 16:16 2018/7/31
 * @修改说明：
 * @修改日期： 16:16 2018/7/31
 * @版本号： V1.0.0
 */
public interface LsbMapper extends BaseMapper<ValidateInfo> {

    Long addValidateInfo(ValidateInfo info);

    void addTransInfos(List<TransactionInfo> transactionInfos);

    List<TransactionInfo> getTransactionInfoList(@Param("validateId") Long validateId);

    ValidateInfo searchValidateInfo(@Param("validateId") Long validateId);

    List<ValidateInfo> getValidateInfoList(@Param("userId") String userId);

    List<MyReportResponse> getMyReportList(@Param("userId") String userId);

    MyReportResponse getMyLastReportList(@Param("userId") String userId);

    List<SendReportResponse> getReportSendMailList(@Param("userId") String userId);

    void insertTransactionInfoList(@Param("list") List<TransactionInfo> transList);

    void insertReceiveInfo(ReceiveInfo info);

    ShowUserEntity getUserInfo(String userId);

    void addSystemUser(ShowUserEntity user);

    void updateWexinUser(ShowUserEntity user);

    void updateUserInfo(ShowUserEntity user);

    Integer checkFlowNo(@Param("flowNo")String flowNo,@Param("userId")String userId);

    Integer insertValidateInfo(@Param("flowNo")String flowNo,@Param("userId")String userId);

    void updateValidateInfo(ValidateInfo validateInfo);

    Integer insertUserEducational(UserEducational userEducational);

    List<UserEducational> getUserEducationalInfos(@Param("userId")String userId);

    List<SupportOperation> searchOperationByType(@Param("type") String type);
}
