package com.osidocker.open.micro.draw.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * <p>
 * 活动类别下的奖品中奖次数统计信息表
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-14
 */
@TableName("hd_active_prize_statistics")
public class ActivePrizeStatistics extends Model<ActivePrizeStatistics> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动Id
     */
    @TableField("class_id")
    private Integer classId;
    /**
     * 活动类型Id
     */
    private Integer type;
    /**
     * 奖品Id
     */
    private Integer prize;
    /**
     * 参与日期
     */
    @TableField("partake_date")
    private String partakeDate;
    /**
     * 中奖次数
     */
    @TableField("prize_access")
    private Integer prizeAccess;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPrize() {
        return prize;
    }

    public void setPrize(Integer prize) {
        this.prize = prize;
    }

    public String getPartakeDate() {
        return partakeDate;
    }

    public void setPartakeDate(String partakeDate) {
        this.partakeDate = partakeDate;
    }

    public Integer getPrizeAccess() {
        return prizeAccess;
    }

    public void setPrizeAccess(Integer prizeAccess) {
        this.prizeAccess = prizeAccess;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActivePrizeStatistics{" +
        "id=" + id +
        ", classId=" + classId +
        ", type=" + type +
        ", prize=" + prize +
        ", partakeDate=" + partakeDate +
        ", prizeAccess=" + prizeAccess +
        "}";
    }
}
