/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.model;

import com.osidocker.open.micro.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 16:50 2018/7/31
 * @修改说明：
 * @修改日期： 16:50 2018/7/31
 * @版本号： V1.0.0
 */
public class TransactionInfo implements Serializable {
    private Logger logger = LoggerFactory.getLogger(TransactionInfo.class);

    private Long txnId;
    private Long validateId;
    private BigDecimal balance;
    private String transTime;
    private String transRemark;
    private String transCurrency;
    private String transMoney;
    private String otherAccountName="";
    private String otherAccount="";
    private String transAddress="";
    private String transType="";
    private boolean isPdfFlag = false;

    public TransactionInfo(boolean isPdfFlag) {
        this.isPdfFlag = isPdfFlag;
    }

    public TransactionInfo() {
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        if( StringUtil.isEmpty(transType) || transType.equalsIgnoreCase("null") ){
            this.transType = "";
        }else{
            this.transType = transType;
        }
    }

    public String getOtherAccount() {
        return otherAccount;
    }

    public String getOtherAccountStr(){
        return StringUtil.around(otherAccount,4,4);
    }

    public void setOtherAccount(String otherAccount) {
        if( StringUtil.isEmpty(otherAccount) || "null".equalsIgnoreCase(otherAccount) ){
            this.otherAccount = "";
        }else{
            this.otherAccount = otherAccount;
        }
    }

    public String getTransAddress() {
        return transAddress;
    }

    public void setTransAddress(String transAddress) {
        if( StringUtil.isEmpty(transAddress) || "null".equalsIgnoreCase(transAddress) ){
            this.transAddress = "";
        }else{
            this.transAddress = transAddress;
        }
    }

    public Long getTxnId() {
        return txnId;
    }

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    public Long getValidateId() {
        return validateId;
    }

    public void setValidateId(Long validateId) {
        this.validateId = validateId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        if( !balance.equals(new BigDecimal(0)) ){
            this.balance = balance.divide(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_DOWN);
        }else{
            this.balance = new BigDecimal(0);
        }
    }

    public String getBalanceStr(){
        if( isPdfFlag ){
            return balance.setScale(2).toString();
        }
        return balance.multiply(new BigDecimal(100)).setScale(2).toString();
    }

    public String getTransTime() {
        return transTime;
    }

    public String getTransTimeStr(){
        return transTime.replaceAll("[^a-zA-Z0-9]", "")
                .replaceAll("\\s+", "_").substring(0,8);
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransRemark() {
        return transRemark;
    }

    public void setTransRemark(String transRemark) {
        if( StringUtil.isEmpty(transRemark) || "null".equalsIgnoreCase(transRemark) ){
            this.transRemark = "";
        }else{
            this.transRemark = transRemark;
        }
    }

    public String getTransCurrency() {
        return transCurrency;
    }

    public void setTransCurrency(String transCurrency) {
        this.transCurrency = transCurrency;
    }

    public String getTransMoney() {
        return transMoney;
    }

    public void setTransMoney(String transMoney) {
        if( !transMoney.equals("0") ){
            this.transMoney = new BigDecimal(transMoney).divide(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_DOWN).toString();
        }else{
            this.transMoney = "0";
        }
    }

    public String getTransMoneyStr(){
        if( isPdfFlag ){
            return new BigDecimal(transMoney).setScale(2).toString();
        }
        return new BigDecimal(transMoney).multiply(new BigDecimal(100)).setScale(2).toString();
    }

    public String getOtherAccountName() {
        return otherAccountName;
    }

    public void setOtherAccountName(String otherAccountName) {
        if( StringUtil.isEmpty(otherAccountName) || "null".equalsIgnoreCase(otherAccountName) ){
            this.otherAccountName = "";
        }else{
            this.otherAccountName = otherAccountName;
        }
    }
}
