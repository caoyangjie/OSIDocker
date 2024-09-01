package com.osidocker.open.micro.utils.halo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @className: TagEntity
 * @description:
 * @author: caoyangjie
 * @date: 2024/9/1
 **/
@Data
public class TagEntity {
    private String tagName;
    private String slugId;
    private String metadataName;
    private JSONObject akd;
}
