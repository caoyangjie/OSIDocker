package com.osidocker.open.micro.utils.halo;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.*;

/**
 * @className: GenerateSoloMarkdown
 * @description:
 * @author: caoyangjie
 * @date: 2024/8/31
 **/
public class ImportHaloMarkdown {
    public static TokenInfo server = new TokenInfo() {
        @Override
        public String getToken() {
            return "b6d4611a-ce53-4bf4-8f45-1233f827582c";
        }

        @Override
        public String getCookie() {
            return "device_id=c1bc1514256e4172aa6524cbdd93653a; XSRF-TOKEN=b6d4611a-ce53-4bf4-8f45-1233f827582c; remember-me=S1ptQ3VhTzJiQ3Y0MnFNZWhTTWFSQSUzRCUzRDpuRnI1UyUyQnhBTlpnSzk5T01OdFFpb1ElM0QlM0Q; SESSION=77b5cba0-a0d9-4cf1-989e-469cd5623c83";
        }

        @Override
        public String getDomain() {
            return "http://www.yqyl.tech";
        }
    };
    public static void main(String[] args) {
//        HaloQueryWithEntities.generate();
        String markdown = "# Hello, CommonMark!\n" +
                "This is a paragraph with **bold** text and _italic_ text.\n" +
                "Here is a list:\n" +
                "- Item 1\n" +
                "- Item 2\n" +
                "- Item 3\n";
//        createArticle("b6d4611a-ce53-4bf4-8f45-1233f827582c", cookie, "test", "12345677890", markdown);
        ArticleEntity entity = new ArticleEntity();
        entity.setTitle("测试java代码创建");
        entity.setSlugId("1234567890");
        entity.setMetadataName(UUID.randomUUID().toString());
        entity.setContent(markdown);
        ArticleStore store = new ArticleStore();
        store.create(entity);
    }

    public static String md2Html(String content) {
        Node document = parser.parse(content);
        // 将节点渲染为 HTML
        return  renderer.render(document);
    }

    // 创建 Markdown 解析器实例
    static Parser parser = Parser.builder().build();
    // 创建 HTML 渲染器实例
    static HtmlRenderer renderer = HtmlRenderer.builder().build();
}
