package com.osidocker.open.micro.draw.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 获奖表
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@TableName("hd_active_winning")
public class ActiveWinning extends Model<ActiveWinning> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动类型ID
     */
    private Integer type;
    /**
     * 奖池分类
     */
    @TableField("class_id")
    private Integer classId;
    /**
     * 用户ID
     */
    private Integer uid;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 获奖时间
     */
    @TableField("win_time")
    private Date winTime;
    /**
     * 奖品类型
     */
    @TableField("prize_type")
    private Integer prizeType;
    /**
     * 奖品ID
     */
    @TableField("prize_id")
    private Integer prizeId;
    /**
     * 奖品名称
     */
    @TableField("prize_name")
    private String prizeName;
    /**
     * 奖品图片
     */
    @TableField("prize_pic")
    private String prizePic;
    /**
     * 发放数量
     */
    private Integer amount;
    /**
     * 姓名
     */
    private String realname;
    /**
     * 电话
     */
    private String telphone;
    /**
     * 地址
     */
    private String address;
    /**
     * 0未领取，1已领取
     */
    private Integer status;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 领取时间
     */
    @TableField("sub_time")
    private Date subTime;
    /**
     * 券名称
     */
    private String welfare;
    /**
     * 福利类型
     */
    @TableField("welfare_type")
    private Integer welfareType;
    /**
     * 券ID
     */
    @TableField("ticket_id")
    private String ticketId;
    /**
     * 券价值
     */
    @TableField("ticket_amount")
    private Double ticketAmount;
    /**
     * 适用产品
     */
    @TableField("app_products")
    private String appProducts;
    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;
    /**
     * 交易号
     */
    @TableField("trade_no")
    private String tradeNo;
    /**
     * -1免费，1收费
     */
    private Integer isfree;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getWinTime() {
        return winTime;
    }

    public void setWinTime(Date winTime) {
        this.winTime = winTime;
    }

    public Integer getPrizeType() {
        return prizeType;
    }

    public void setPrizeType(Integer prizeType) {
        this.prizeType = prizeType;
    }

    public Integer getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Integer prizeId) {
        this.prizeId = prizeId;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public String getPrizePic() {
        return prizePic;
    }

    public void setPrizePic(String prizePic) {
        this.prizePic = prizePic;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getSubTime() {
        return subTime;
    }

    public void setSubTime(Date subTime) {
        this.subTime = subTime;
    }

    public String getWelfare() {
        return welfare;
    }

    public void setWelfare(String welfare) {
        this.welfare = welfare;
    }

    public Integer getWelfareType() {
        return welfareType;
    }

    public void setWelfareType(Integer welfareType) {
        this.welfareType = welfareType;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Double getTicketAmount() {
        return ticketAmount;
    }

    public void setTicketAmount(Double ticketAmount) {
        this.ticketAmount = ticketAmount;
    }

    public String getAppProducts() {
        return appProducts;
    }

    public void setAppProducts(String appProducts) {
        this.appProducts = appProducts;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Integer getIsfree() {
        return isfree;
    }

    public void setIsfree(Integer isfree) {
        this.isfree = isfree;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActiveWinning{" +
        "id=" + id +
        ", type=" + type +
        ", classId=" + classId +
        ", uid=" + uid +
        ", nickname=" + nickname +
        ", winTime=" + winTime +
        ", prizeType=" + prizeType +
        ", prizeId=" + prizeId +
        ", prizeName=" + prizeName +
        ", prizePic=" + prizePic +
        ", amount=" + amount +
        ", realname=" + realname +
        ", telphone=" + telphone +
        ", address=" + address +
        ", status=" + status +
        ", ip=" + ip +
        ", subTime=" + subTime +
        ", welfare=" + welfare +
        ", welfareType=" + welfareType +
        ", ticketId=" + ticketId +
        ", ticketAmount=" + ticketAmount +
        ", appProducts=" + appProducts +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", tradeNo=" + tradeNo +
        ", isfree=" + isfree +
        "}";
    }
}
