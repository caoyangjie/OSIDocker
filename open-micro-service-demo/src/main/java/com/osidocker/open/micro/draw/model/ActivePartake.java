package com.osidocker.open.micro.draw.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 活动参与次数表
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@TableName("hd_active_partake")
public class ActivePartake extends Model<ActivePartake> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动类型
     */
    private Integer type;
    /**
     * 活动分类
     */
    @TableField("class_id")
    private Integer classId;
    /**
     * 日期
     */
    private String date;
    /**
     * 每日次数
     */
    private Integer visit;


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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActivePartake{" +
        "id=" + id +
        ", type=" + type +
        ", classId=" + classId +
        ", date=" + date +
        ", visit=" + visit +
        "}";
    }
}
