/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.model;

import java.io.Serializable;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 15:19 2018/9/4
 * @修改说明：
 * @修改日期： 15:19 2018/9/4
 * @版本号： V1.0.0
 */
public class SupportOperation implements Serializable {

    private Long operId;
    private String pythonOld;
    private String businessName;
    private String businessType;
    private String status;

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getPythonOld() {
        return pythonOld;
    }

    public void setPythonOld(String pythonOld) {
        this.pythonOld = pythonOld;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
