/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.entity;

import com.osidocker.open.micro.utils.StringUtil;

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
public class MyReportResponse extends ReportResponse{
    String custName;
    String accountNo;
    String bankName;
    String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getAccountNo() {
        return StringUtil.around(Optional.ofNullable(accountNo).orElse(""),4,4);
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String getShowName() {
        return this.getBankName();
    }
}
