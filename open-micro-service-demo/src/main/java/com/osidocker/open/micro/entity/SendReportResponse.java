/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.entity;

import java.util.Optional;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 9:40 2018/8/3
 * @修改说明：
 * @修改日期： 9:40 2018/8/3
 * @版本号： V1.0.0
 */
public class SendReportResponse extends ReportResponse{

    Long reportId;
    String receiveMail;
    String receiveName;
    String status;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getReceiveMail() {
        return receiveMail;
    }

    public void setReceiveMail(String receiveMail) {
        this.receiveMail = receiveMail;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getShowName() {
        return Optional.ofNullable(this.getReceiveName()).orElse(this.getReceiveMail());
    }
}
