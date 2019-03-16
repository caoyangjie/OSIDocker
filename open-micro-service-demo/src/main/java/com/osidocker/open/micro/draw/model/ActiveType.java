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
 * 抽奖活动列表
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@TableName("hd_active_type")
public class ActiveType extends Model<ActiveType> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动类型
     */
    private String name;
    /**
     * 创建时间 
     */
    private Date date;
    /**
     * 内容
     */
    private String content;
    /**
     * 活动ID
     */
    @TableField("active_id")
    private Integer activeId;
    /**
     * 口令
     */
    private String password;
    /**
     * 是否有时间限制
     */
    @TableField("is_tlimit")
    private Integer isTlimit;
    /**
     * 是否指定次数出奖
     */
    @TableField("is_visit")
    private Integer isVisit;
    /**
     * 是否直接发放
     */
    @TableField("is_grant")
    private Integer isGrant;
    /**
     * 百分百中奖
     */
    @TableField("is_all")
    private Integer isAll;
    /**
     * 概率总和
     */
    @TableField("chance_sum")
    private Integer chanceSum;
    /**
     * 开始时间
     */
    private Date stime;
    /**
     * 结束时间
     */
    private Date etime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsTlimit() {
        return isTlimit;
    }

    public void setIsTlimit(Integer isTlimit) {
        this.isTlimit = isTlimit;
    }

    public Integer getIsVisit() {
        return isVisit;
    }

    public void setIsVisit(Integer isVisit) {
        this.isVisit = isVisit;
    }

    public Integer getIsGrant() {
        return isGrant;
    }

    public void setIsGrant(Integer isGrant) {
        this.isGrant = isGrant;
    }

    public Integer getIsAll() {
        return isAll;
    }

    public void setIsAll(Integer isAll) {
        this.isAll = isAll;
    }

    public Integer getChanceSum() {
        return chanceSum;
    }

    public void setChanceSum(Integer chanceSum) {
        this.chanceSum = chanceSum;
    }

    public Date getStime() {
        return stime;
    }

    public void setStime(Date stime) {
        this.stime = stime;
    }

    public Date getEtime() {
        return etime;
    }

    public void setEtime(Date etime) {
        this.etime = etime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActiveType{" +
        "id=" + id +
        ", name=" + name +
        ", date=" + date +
        ", content=" + content +
        ", activeId=" + activeId +
        ", password=" + password +
        ", isTlimit=" + isTlimit +
        ", isVisit=" + isVisit +
        ", isGrant=" + isGrant +
        ", isAll=" + isAll +
        ", chanceSum=" + chanceSum +
        ", stime=" + stime +
        ", etime=" + etime +
        "}";
    }
}
