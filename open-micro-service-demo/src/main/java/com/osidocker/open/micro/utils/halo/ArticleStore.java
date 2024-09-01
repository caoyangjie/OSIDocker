package com.osidocker.open.micro.utils.halo;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.osidocker.open.micro.utils.StringUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.osidocker.open.micro.utils.halo.ImportHaloMarkdown.md2Html;

@Data
class ArticleStore extends HaloStore {

    private Map<String, ArticleEntity> cache = new HashMap<>();
    private TagStore tagStore = new TagStore();
    private CategoriesStore categoriesStore = new CategoriesStore();
    private String postUri = ImportHaloMarkdown.server.getDomain().concat("/apis/api.console.halo.run/v1alpha1/posts");
    private String putUri = ImportHaloMarkdown.server.getDomain().concat("/apis/content.halo.run/v1alpha1/posts/{}");
    private String contentUri = ImportHaloMarkdown.server.getDomain().concat("/apis/api.console.halo.run/v1alpha1/posts/{}/content");
    private String pageUri = ImportHaloMarkdown.server.getDomain().concat("/apis/api.console.halo.run/v1alpha1/posts?page={}&size={}&labelSelector=content.halo.run%2Fdeleted%3Dfalse&keyword=");
    private String postTemplate = "{\n" +
            "    \"post\": {\n" +
            "        \"spec\": {\n" +
            "            \"title\": \"{}\",\n" +
            "            \"slug\": \"{}\",\n" +
            "            \"template\": \"\",\n" +
            "            \"cover\": \"\",\n" +
            "            \"deleted\": false,\n" +
            "            \"publish\": false,\n" +
            "            \"pinned\": false,\n" +
            "            \"allowComment\": true,\n" +
            "            \"visible\": \"PUBLIC\",\n" +
            "            \"priority\": 0,\n" +
            "            \"excerpt\": {\n" +
            "                \"autoGenerate\": true,\n" +
            "                \"raw\": \"\"\n" +
            "            },\n" +
            "            \"categories\": [],\n" +
            "            \"tags\": [],\n" +
            "            \"htmlMetas\": []\n" +
            "        },\n" +
            "        \"apiVersion\": \"content.halo.run/v1alpha1\",\n" +
            "        \"kind\": \"Post\",\n" +
            "        \"metadata\": {\n" +
            "            \"name\": \"{}\",\n" +
            "            \"annotations\": {\n" +
            "                \"content.halo.run/preferred-editor\": \"stackedit\"\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"content\": {\n" +
            "        \"raw\": \"\",\n" +
            "        \"content\": \"\",\n" +
            "        \"rawType\": \"markdown\"\n" +
            "    }\n" +
            "}";

    private void post(ArticleEntity article) {
        String postData = StrFormatter.format(getPostTemplate(), article.getTitle(), article.getSlugId(), article.getMetadataName() );

        String snapshotId = post(postUri, ImportHaloMarkdown.server, ()->postData, rspData->{
            return JSONObject.parseObject(rspData).getJSONObject("spec").getString("baseSnapshot");
        });
        article.setSnapshotId(snapshotId);
    }

    private void put(ArticleEntity article){
        Content content = new Content();
        content.setSnapshotName(article.getSnapshotId());
        content.setRaw(article.getContent());
        content.setContent(md2Html(article.getContent()));
        content.setRawType("markdown");
        String putUri = StrFormatter.format(contentUri, article.getMetadataName());
        put(putUri, ImportHaloMarkdown.server, ()->JSONObject.toJSONString(content), rspData->{
            return rspData;
        });
    }

    /**
     * 创建 博客文章
     * @param article
     */
    public void create(ArticleEntity article) {
        if( cache.isEmpty() ) {
            page(0,0, article.getTitle());
        }
        if( cache.containsKey(article.getTitle()) ) {
            System.err.println(StrFormatter.format("系统中已经存在一篇名为：[ {} ] 的博客文章,请检查是否重复!", article.getTitle()));
            return;
        }
        post(article);
        put(article);
        // 更新 tag categories 信息
        if( !StringUtil.isEmpty(article.getTags()) || !StringUtil.isEmpty(article.getCategories()) ) {
            page(0,0, article.getTitle());
            if( cache.containsKey(article.getTitle()) ) {
                ArticleEntity entity = cache.get(article.getTitle());
                if( !StringUtil.isEmpty(article.getTags()) ) {
                    Splitter.on(",").trimResults().omitEmptyStrings().splitToList(article.getTags())
                        .stream().forEach(tag->{
                            tagStore.create(tag);
                        }
                    );
                    Splitter.on(",").splitToList(tagStore.displayName2MetadataName(article.getTags())).forEach(tagMetaName->{
                       JSONArray cates = entity.getAkd().getJSONObject("spec").getJSONArray("tags");
                       cates.add(tagMetaName);
                    });
                }
                if( !StringUtil.isEmpty(article.getCategories()) ) {
                    Splitter.on(",").trimResults().omitEmptyStrings().splitToList(article.getCategories())
                            .stream().forEach(cate->{
                                        categoriesStore.create(cate);
                                    }
                            );
                    Splitter.on(",").splitToList(categoriesStore.displayName2MetadataName(article.getCategories())).forEach(cateMetaName->{
                        JSONArray cates = entity.getAkd().getJSONObject("spec").getJSONArray("categories");
                        cates.add(cateMetaName);
                    });
                }
                String putUri = StrFormatter.format(getPutUri(), entity.getMetadataName());
                // 更新 tag 和 categories
                put(putUri, ImportHaloMarkdown.server, ()->JSONObject.toJSONString(entity.getAkd()),rspData->{
                    return rspData;
                });
            }
        }
    }

    public Map<String,ArticleEntity> page(int page, int size, String search) {
        String listUri = StrFormatter.format(pageUri, page<=0?1:page, size<=0?2000:size, StringUtil.isEmpty(search)?"":search);
        cache = list(listUri, ImportHaloMarkdown.server, rspData->{
            Map<String, ArticleEntity> articleEntityMap = new HashMap<>();
            JSONArray items = JSONObject.parseObject(rspData).getJSONArray("items");
            items.stream().forEach(itemO->{
                JSONObject item = (JSONObject) itemO;
                JSONObject akd = item.getJSONObject("post");
                ArticleEntity entity = new ArticleEntity();
                entity.setAkd(akd);
                entity.setSlugId(akd.getJSONObject("spec").getString("slug"));
                entity.setTitle(akd.getJSONObject("spec").getString("title"));
                entity.setMetadataName(akd.getJSONObject("metadata").getString("name"));
                JSONArray cate = akd.getJSONObject("spec").getJSONArray("categories");
                if( cate!=null && !cate.isEmpty() ) {
                    entity.setCategories(cate.stream().map(v->v.toString()).collect(Collectors.joining(",")));
                }
                JSONArray tag = akd.getJSONObject("spec").getJSONArray("tags");
                if( tag!=null && !tag.isEmpty() ) {
                    entity.setTags(tag.stream().map(v->v.toString()).collect(Collectors.joining(",")));
                }
                articleEntityMap.put(entity.getTitle(), entity);
            });
            return articleEntityMap;
        });
        return cache;
    }
    public ArticleStore() {
        tagStore.list(0,0,"");
        categoriesStore.list();
    }

    public static void main(String[] args) {
        String markdown = "# Hello, CommonMark!\n" +
                "This is a paragraph with **bold** text and _italic_ text.\n" +
                "Here is a list:\n" +
                "- Item 1\n" +
                "- Item 2\n" +
                "- Item 3\n";
        ArticleStore store = new ArticleStore();
        ArticleEntity entity = new ArticleEntity();
        entity.setTitle("测试java代码创建");
        entity.setSlugId("1234567890");
        entity.setTags("新增一个tag,新增两个tag");
        entity.setCategories("新增一个cate,新增两个cate");
        entity.setMetadataName(UUID.randomUUID().toString());
        entity.setContent(markdown);
        store.create(entity);
    }
}
