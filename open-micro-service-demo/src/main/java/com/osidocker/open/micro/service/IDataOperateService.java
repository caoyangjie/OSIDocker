/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.service;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于10:48 2018/3/8
 * @修改说明：
 * @修改日期： 修改于10:48 2018/3/8
 * @版本号： V1.0.0
 */
public interface IDataOperateService<ResponseEntity> {

    /**
     * 更新展示对象
     * @param responseEntity 展示对象
     */
    void updateResponseEntity(ResponseEntity responseEntity);

    /**
     * 移除展示对象
     * @param responseEntity
     */
    void removeResponseEntityCache(ResponseEntity responseEntity);

    /**
     * 查找展示对象
     * @param id    数据id
     * @param requestEntity     其他相关信息字段
     * @return
     */
    <RequestEntity> ResponseEntity findResponseEntity(Long id, RequestEntity requestEntity);

    /**
     * 缓存展示对象
     * @param responseEntity
     */
    void setResponseEntity(ResponseEntity responseEntity);

    /**
     * 获取缓存展示对象
     * @param id
     * @return
     */
    ResponseEntity getResponseEntityCache(Long id);
}
