/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.entity;

import com.osidocker.open.micro.model.TransactionInfo;
import com.osidocker.open.micro.utils.StringUtil;

import java.util.List;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 18:31 2018/8/1
 * @修改说明：
 * @修改日期： 18:31 2018/8/1
 * @版本号： V1.0.0
 */
public class Report {
    private String bankName;
    private String startDate;
    private String endDate;
    private String userName;
    private String idCard;
    private String accountNo;
    private String counts;
    private List<TransactionInfo> transList;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getUserName() {
        return StringUtil.left(userName,1);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAccountNo() {
        return StringUtil.around(accountNo,4,4);
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    public List<TransactionInfo> getTransList() {
        return transList;
    }

    public void setTransList(List<TransactionInfo> transList) {
        if( transList!=null ){
            setCounts(transList.size()+"");
            setStartDate(transList.get(0).getTransTime().replaceAll("[^a-zA-Z0-9]", "")
                    .replaceAll("\\s+", "_").substring(0,8));
            setEndDate(transList.get(transList.size()-1).getTransTime().replaceAll("[^a-zA-Z0-9]", "")
                    .replaceAll("\\s+", "_").substring(0,8));
        }
        this.transList = transList;
    }
}
