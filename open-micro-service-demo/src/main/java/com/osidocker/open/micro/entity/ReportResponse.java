/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.entity;

import java.util.Date;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 15:26 2018/8/2
 * @修改说明：
 * @修改日期： 15:26 2018/8/2
 * @版本号： V1.0.0
 */
public class ReportResponse {
    Long validateId;
    String pdfUrl;
    String showName;
    Date createDate;
    Long custId;

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getValidateId() {
        return validateId;
    }

    public void setValidateId(Long validateId) {
        this.validateId = validateId;
    }

    public String getPdfUrl() {
//        TODO 测试代码
//        return PropertiesContext.getString("creditmate.pdf.base.url")+pdfUrl+".pdf";
//        return "http://192.168.188.100:8687/yanshu-app-web/static/pdfDemo_water.pdf";
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}
