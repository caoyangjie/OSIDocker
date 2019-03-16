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
@TableName("hd_active")
public class Active extends Model<Active> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 创建时间 
     */
    private Date date;
    /**
     * 发布时间
     */
    @TableField("release_date")
    private Date releaseDate;
    /**
     * 内容
     */
    private String content;
    /**
     * 0为未发布 1发布 
     */
    private Integer status;
    /**
     * 是否需要用户信息
     */
    @TableField("is_user")
    private Integer isUser;


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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsUser() {
        return isUser;
    }

    public void setIsUser(Integer isUser) {
        this.isUser = isUser;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Active{" +
        "id=" + id +
        ", name=" + name +
        ", date=" + date +
        ", releaseDate=" + releaseDate +
        ", content=" + content +
        ", status=" + status +
        ", isUser=" + isUser +
        "}";
    }
}
