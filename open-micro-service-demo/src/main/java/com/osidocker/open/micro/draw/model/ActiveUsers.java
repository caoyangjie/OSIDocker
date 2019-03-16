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
 * 参与用户表
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@TableName("hd_active_users")
public class ActiveUsers extends Model<ActiveUsers> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动类型ID
     */
    private Integer type;
    /**
     * 用户ID
     */
    private Integer uid;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 真实姓名
     */
    private String realname;
    /**
     * 用户图像
     */
    private String avatar;
    /**
     * 年化投资
     */
    private Float alldenomination;
    /**
     * 剩余飞镖数
     */
    @TableField("dart_num")
    private Integer dartNum;
    /**
     * 已用飞镖数
     */
    @TableField("dart_use")
    private Integer dartUse;
    /**
     * 总飞镖数
     */
    @TableField("dart_total")
    private Integer dartTotal;
    /**
     * 剩余门票数
     */
    @TableField("ticket_num")
    private Integer ticketNum;
    /**
     * 已用门票数
     */
    @TableField("ticket_use")
    private Integer ticketUse;
    /**
     * 总门票数
     */
    @TableField("ticket_total")
    private Integer ticketTotal;
    /**
     * 射击次数
     */
    @TableField("fire_count")
    private Integer fireCount;
    /**
     * 参与时间
     */
    private Date addtime;
    private String ip;
    /**
     * 今日抽奖次数
     */
    @TableField("today_num")
    private Integer todayNum;
    /**
     * WAP端每日中奖次数
     */
    @TableField("wap_num")
    private Integer wapNum;
    /**
     * 总投资金额
     */
    @TableField("investment_denomination")
    private Float investmentDenomination;
    /**
     * 活动分类
     */
    private Integer classid;


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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Float getAlldenomination() {
        return alldenomination;
    }

    public void setAlldenomination(Float alldenomination) {
        this.alldenomination = alldenomination;
    }

    public Integer getDartNum() {
        return dartNum;
    }

    public void setDartNum(Integer dartNum) {
        this.dartNum = dartNum;
    }

    public Integer getDartUse() {
        return dartUse;
    }

    public void setDartUse(Integer dartUse) {
        this.dartUse = dartUse;
    }

    public Integer getDartTotal() {
        return dartTotal;
    }

    public void setDartTotal(Integer dartTotal) {
        this.dartTotal = dartTotal;
    }

    public Integer getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(Integer ticketNum) {
        this.ticketNum = ticketNum;
    }

    public Integer getTicketUse() {
        return ticketUse;
    }

    public void setTicketUse(Integer ticketUse) {
        this.ticketUse = ticketUse;
    }

    public Integer getTicketTotal() {
        return ticketTotal;
    }

    public void setTicketTotal(Integer ticketTotal) {
        this.ticketTotal = ticketTotal;
    }

    public Integer getFireCount() {
        return fireCount;
    }

    public void setFireCount(Integer fireCount) {
        this.fireCount = fireCount;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getTodayNum() {
        return todayNum;
    }

    public void setTodayNum(Integer todayNum) {
        this.todayNum = todayNum;
    }

    public Integer getWapNum() {
        return wapNum;
    }

    public void setWapNum(Integer wapNum) {
        this.wapNum = wapNum;
    }

    public Float getInvestmentDenomination() {
        return investmentDenomination;
    }

    public void setInvestmentDenomination(Float investmentDenomination) {
        this.investmentDenomination = investmentDenomination;
    }

    public Integer getClassid() {
        return classid;
    }

    public void setClassid(Integer classid) {
        this.classid = classid;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActiveUsers{" +
        "id=" + id +
        ", type=" + type +
        ", uid=" + uid +
        ", nickname=" + nickname +
        ", realname=" + realname +
        ", avatar=" + avatar +
        ", alldenomination=" + alldenomination +
        ", dartNum=" + dartNum +
        ", dartUse=" + dartUse +
        ", dartTotal=" + dartTotal +
        ", ticketNum=" + ticketNum +
        ", ticketUse=" + ticketUse +
        ", ticketTotal=" + ticketTotal +
        ", fireCount=" + fireCount +
        ", addtime=" + addtime +
        ", ip=" + ip +
        ", todayNum=" + todayNum +
        ", wapNum=" + wapNum +
        ", investmentDenomination=" + investmentDenomination +
        ", classid=" + classid +
        "}";
    }
}
