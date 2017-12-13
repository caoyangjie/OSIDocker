/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.entity;

import com.osidocker.open.micro.utils.StringUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于17:42 2017/3/15
 * @修改说明：
 * @修改日期： 修改于17:42 2017/3/15
 * @版本号： V1.0.0
 */
public class CoreEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String uid = StringUtil.get32UUID();// 主键ID.
    private Integer version = 0;// 版本号默认为0
    private String status;// 状态 PublicStatusEnum
    private String creater;// 创建人.
    private Date createTime = new Date();// 创建时间.
    private String editor;// 修改人.
    private Date editTime;// 修改时间.
    private String remark;// 描述

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
