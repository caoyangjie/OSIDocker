/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.model.ProductInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 11:54 2018/9/17
 * @修改说明：
 * @修改日期： 11:54 2018/9/17
 * @版本号： V1.0.0
 */
public interface LoanApplyMapper extends BaseMapper<ProductInfo> {

    /**
     * 更新进件信息表当前状态
     * @param applyId
     * @param status
     * @return
     */
    int updateLoanApplyStatus(@Param("applyId") String applyId, @Param("status") String status);

    /**
     * 获取关联信息表ID
     * @param orderId
     * @return
     */
    String selectApplyId(@Param("orderId") String orderId);
}
