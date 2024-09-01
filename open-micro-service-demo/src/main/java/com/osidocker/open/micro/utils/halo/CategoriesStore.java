package com.osidocker.open.micro.utils.halo;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.osidocker.open.micro.utils.StringUtil;
import lombok.Data;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Data
class CategoriesStore extends HaloStore{
    private Map<String, CategoriesEntity> cache = new HashMap<>();
    private final String postUri = ImportHaloMarkdown.server.getDomain().concat("/apis/content.halo.run/v1alpha1/categories");
    private final String putUri = ImportHaloMarkdown.server.getDomain().concat("/apis/content.halo.run/v1alpha1/categories/{}");
    private final String pageUri = ImportHaloMarkdown.server.getDomain().concat("/apis/content.halo.run/v1alpha1/categories?page=0&size=2000&sort=metadata.creationTimestamp%2Cdesc");

    private String postTemplate = "{\n" +
            "  \"spec\": {\n" +
            "    \"displayName\": \"{}\",\n" +
            "    \"slug\": \"{}\",\n" +
            "    \"description\": \"\",\n" +
            "    \"cover\": \"\",\n" +
            "    \"template\": \"\",\n" +
            "    \"priority\": 4,\n" +
            "    \"children\": []\n" +
            "  },\n" +
            "  \"apiVersion\": \"content.halo.run/v1alpha1\",\n" +
            "  \"kind\": \"Category\",\n" +
            "  \"metadata\": {\n" +
            "    \"name\": \"\",\n" +
            "    \"generateName\": \"category-\"\n" +
            "  }\n" +
            "}";

    public String create(String categoriesName) {
        if( cache.isEmpty() ) {
            list();
        }
        if( cache.containsKey(categoriesName) ) {
            return cache.get(categoriesName).getMetadataName();
        }
        String postData = StrFormatter.format(postTemplate, categoriesName, StringUtil.hashString(categoriesName));
        String metadataName = post(postUri, ImportHaloMarkdown.server, ()->postData, rspData->{
            return JSONObject.parseObject(rspData).getJSONObject("metadata").getString("name");
        });
        list();
        return metadataName;
    }

    public void update(String categoriesName, String subCategoriesName) {
        if( !cache.containsKey(categoriesName) || !cache.containsKey(subCategoriesName) ){
            list();
        }
        boolean needUpdate = false;
        if( !cache.containsKey(subCategoriesName) ){
            create(subCategoriesName);
            needUpdate = true;
        }
        if( !cache.containsKey(categoriesName) ){
            create(categoriesName);
            needUpdate = true;
        }
        if( needUpdate ) {
            list();
        }
        CategoriesEntity entity = cache.get(categoriesName);
        String putUri = StrFormatter.format(getPutUri(),entity.getMetadataName());
        entity.getAkd().getJSONObject("spec").getJSONArray("children").add(cache.get(subCategoriesName).getMetadataName());
        put(putUri, ImportHaloMarkdown.server, ()-> JSONObject.toJSONString(entity.getAkd()), rspData->{
            return rspData;
        });
    }

    public Map<String,CategoriesEntity> list() {
        cache = list(pageUri, ImportHaloMarkdown.server, rspData->{
            JSONArray items = JSONObject.parseObject(rspData).getJSONArray("items");
            Map<String, CategoriesEntity> categoriesEntityMap = new HashMap<>();
            for( Object itemO : items ) {
                JSONObject item = (JSONObject) itemO;
                CategoriesEntity categories = new CategoriesEntity();
                categories.setDisplayName(item.getJSONObject("spec").getString("displayName"));
                categories.setSlugId(item.getJSONObject("spec").getString("slug"));
                categories.setMetadataName(item.getJSONObject("metadata").getString("name"));
                categories.setAkd(item);
                categoriesEntityMap.put(categories.getDisplayName(), categories);
            }
            return categoriesEntityMap;
        });
        return cache;
    }

    public String displayName2MetadataName(String displayNames) {
        if( !StringUtil.isEmpty(displayNames) ) {
            List<String> displayNameStr = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(displayNames);
            return displayNameStr.stream().map(dn->list().get(dn).getMetadataName()).collect(Collectors.joining(","));
        }
        return displayNames;
    }

    public static void main(String[] args) {
        // 获取 系统中的 所有分类
        CategoriesStore store = new CategoriesStore();
        Map<String,CategoriesEntity> datas = store.list();
        datas.forEach((k,v)->{
            System.out.println(StrFormatter.format("{}->[{}]", k, JSONObject.toJSONString(v)));
        });
        // 创建一个新的分类
//        System.out.println(StrFormatter.format("新分类metadataName->{}",store.create("哈哈,一个新分类耶!")));
        store.update("宗父亲","是儿子");
    }
}
