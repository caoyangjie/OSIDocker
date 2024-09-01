package com.osidocker.open.micro.utils.halo;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.osidocker.open.micro.utils.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: TagStore
 * @description:
 * @author: caoyangjie
 * @date: 2024/9/1
 **/
@Data
public class TagStore extends HaloStore{
    private Map<String, TagEntity> cache = new HashMap<>();
    private final String post = ImportHaloMarkdown.server.getDomain().concat("/apis/content.halo.run/v1alpha1/tags");
    private final String list = ImportHaloMarkdown.server.getDomain().concat("/apis/api.console.halo.run/v1alpha1/tags?page={}&size={}&keyword={}");
    private final String delete = ImportHaloMarkdown.server.getDomain().concat("/apis/content.halo.run/v1alpha1/tags/{}");

    private String postTemplate = "{\n" +
            "  \"spec\": {\n" +
            "    \"displayName\": \"{}\",\n" +
            "    \"slug\": \"{}\",\n" +
            "    \"color\": \"#ffffff\",\n" +
            "    \"cover\": \"\"\n" +
            "  },\n" +
            "  \"apiVersion\": \"content.halo.run/v1alpha1\",\n" +
            "  \"kind\": \"Tag\",\n" +
            "  \"metadata\": {\n" +
            "    \"name\": \"\",\n" +
            "    \"generateName\": \"tag-\",\n" +
            "    \"annotations\": {}\n" +
            "  }\n" +
            "}";

    public String create(String tagName) {
        if( cache.isEmpty() ) {
            list(0,0,"");
        }
        if( cache.containsKey(tagName) ) {
            return cache.get(tagName).getMetadataName();
        }
        String reqData = StrFormatter.format(postTemplate, tagName, StringUtil.hashString(tagName));
        String metadataName = post(post, ImportHaloMarkdown.server, ()->reqData, rspData->{
            return JSONObject.parseObject(rspData).getJSONObject("metadata").getString("name");
        });
        list(0,0,"");
        return metadataName;
    }

    public String displayName2MetadataName(String displayNames) {
        if( !StringUtil.isEmpty(displayNames) ) {
            List<String> displayNameStr = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(displayNames);
            return displayNameStr.stream().map(dn->cache.get(dn).getMetadataName()).collect(Collectors.joining(","));
        }
        return displayNames;
    }

    public void delete(String tagName) {
    }

    public Map<String,TagEntity> list(int size, int page, String search) {
        cache = list(StrFormatter.format(list, page<=0?1:page, size<=0?5000:size, StringUtil.isEmpty(search)?"":search), ImportHaloMarkdown.server, rspData->{
            Map<String,TagEntity> tags = new HashMap<>();
            JSONArray items = JSONObject.parseObject(rspData).getJSONArray("items");
            items.stream().forEach(itemO->{
                JSONObject item = (JSONObject) itemO;
                TagEntity tag = new TagEntity();
                tag.setTagName(item.getJSONObject("spec").getString("displayName"));
                tag.setSlugId(item.getJSONObject("spec").getString("slug"));
                tag.setMetadataName(item.getJSONObject("metadata").getString("name"));
                tag.setAkd(item);
                tags.put(tag.getTagName(), tag);
            });
            return tags;
        });
        return cache;
    }

    public static void main(String[] args) {
        TagStore store = new TagStore();
        store.list(0,0,"");
        store.getCache().forEach((k,v)->{
            System.out.println(StrFormatter.format("{}->{}",k,JSONObject.toJSON(v)));
        });
        store.create("这是一个tag");
        store.getCache().forEach((k,v)->{
            System.out.println(StrFormatter.format("{}->{}",k,JSONObject.toJSON(v)));
        });
    }
}
