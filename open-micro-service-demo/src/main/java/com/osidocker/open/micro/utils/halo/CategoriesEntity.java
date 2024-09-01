package com.osidocker.open.micro.utils.halo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: CategoriesEntity
 * @description:
 * @author: caoyangjie
 * @date: 2024/9/1
 **/
@Data
public class CategoriesEntity {
    private String displayName;
    private String slugId;
    private String metadataName;
    private JSONObject akd;

}
