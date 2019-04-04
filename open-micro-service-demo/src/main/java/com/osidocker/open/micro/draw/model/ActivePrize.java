package com.osidocker.open.micro.draw.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * <p>
 * 奖品设置表
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
@TableName("hd_active_prize")
public class ActivePrize extends Model<ActivePrize> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 活动类型
     */
    private Integer type;
    /**
     * 分类
     */
    @TableField("class_id")
    private Integer classId;
    /**
     * 奖品类型
     */
    private Integer mold;
    /**
     * 奖品名称
     */
    private String name;
    /**
     * 中奖概率
     */
    private Integer chance;
    /**
     * 一次中奖数
     */
    private Integer amount;
    /**
     * 福利ID
     */
    @TableField("welfare_id")
    private Integer welfareId;
    /**
     * 创建时间
     */
    private Date addtime;
    /**
     * 总投放数量
     */
    @TableField("total_num")
    private Integer totalNum;
    /**
     * 每日中奖数量
     */
    @TableField("day_num")
    private Integer dayNum;
    /**
     * 指定对应开奖次数奖品，用","隔开。
     */
    private String visits;
    /**
     * 已发放
     */
    @TableField("over_num")
    private Integer overNum;
    /**
     * 当日中奖次数
     */
    @TableField("today_num")
    private Integer todayNum;
    @TableField("week_num")
    private Integer weekNum;
    @TableField("month_num")
    private Integer monthNum;
    private Integer min;
    private Integer max;
    /**
     * 描述
     */
    private String remark;
    /**
     * 0删除
     */
    private Integer del;
    /**
     * 奖品图片
     */
    @TableField("prize_pic")
    private String prizePic;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String desc;


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

    public Integer getMold() {
        return mold;
    }

    public void setMold(Integer mold) {
        this.mold = mold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChance() {
        return chance;
    }

    public void setChance(Integer chance) {
        this.chance = chance;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getWelfareId() {
        return welfareId;
    }

    public void setWelfareId(Integer welfareId) {
        this.welfareId = welfareId;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getDayNum() {
        return dayNum;
    }

    public void setDayNum(Integer dayNum) {
        this.dayNum = dayNum;
    }

    public String getVisits() {
        return visits;
    }

    public void setVisits(String visits) {
        this.visits = visits;
    }

    public Integer getOverNum() {
        return overNum;
    }

    public void setOverNum(Integer overNum) {
        this.overNum = overNum;
    }

    public Integer getTodayNum() {
        return todayNum;
    }

    public void setTodayNum(Integer todayNum) {
        this.todayNum = todayNum;
    }

    public Integer getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(Integer weekNum) {
        this.weekNum = weekNum;
    }

    public Integer getMonthNum() {
        return monthNum;
    }

    public void setMonthNum(Integer monthNum) {
        this.monthNum = monthNum;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public String getPrizePic() {
        return prizePic;
    }

    public void setPrizePic(String prizePic) {
        this.prizePic = prizePic;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "ActivePrize{" +
        "id=" + id +
        ", type=" + type +
        ", class=" + classId +
        ", mold=" + mold +
        ", name=" + name +
        ", chance=" + chance +
        ", amount=" + amount +
        ", welfareId=" + welfareId +
        ", addtime=" + addtime +
        ", totalNum=" + totalNum +
        ", dayNum=" + dayNum +
        ", visits=" + visits +
        ", overNum=" + overNum +
        ", todayNum=" + todayNum +
        ", weekNum=" + weekNum +
        ", monthNum=" + monthNum +
        ", min=" + min +
        ", max=" + max +
        ", remark=" + remark +
        ", del=" + del +
        ", prizePic=" + prizePic +
        ", sort=" + sort +
        ", desc=" + desc +
        "}";
    }
}
