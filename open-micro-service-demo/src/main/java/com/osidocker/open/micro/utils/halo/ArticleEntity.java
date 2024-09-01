package com.osidocker.open.micro.utils.halo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
class ArticleEntity {
    /**
     * 对齐 title
     */
    private String title;
    /**
     * 对齐 oId
     */
    private String slugId;
    /**
     * 元数据ID
     */
    private String metadataName;
    /**
     * 副本ID
     */
    private String snapshotId;
    /**
     * 转换为 html 格式的内容
     */
    private String content;
    /**
     * 分类
     */
    private String categories;
    /**
     * 标签
     */
    private String tags;
    /**
     * 可操作对象描述
     */
    private JSONObject akd;

//    public String getTitle() {
//        try {
//            return URLEncoder.encode(title, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//        }
//        return title;
//    }
}
