package com.osidocker.open.micro.utils;
import cn.hutool.core.text.StrFormatter;
import com.google.common.base.Splitter;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className: GenerateSoloMarkdown
 * @description:
 * @author: caoyangjie
 * @date: 2024/8/31
 **/
public class GenerateSoloMarkdown {
    public static void main(String[] args) {
        MySQLQueryWithEntities.generate();
    }
}


class MySQLQueryWithEntities {

    private static final String URL = "jdbc:mysql://localhost:3306/solo?useSSL=false"; // 数据库URL
    private static final String USER = "root"; // 数据库用户名
    private static final String PASSWORD = "password"; // 数据库密码

    public static void generate() {
        List<Article> blogs = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // 1. 注册JDBC驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 打开连接
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // 3. 创建语句对象
            statement = connection.createStatement();
            // 4. 执行查询
            String sql = "select * from b3_solo_article where articleTags in (select d.articleTags from (SELECT articleTags, COUNT(*) AS counts\n" +
                    "FROM b3_solo_article\n" +
                    "GROUP BY articleTags\n" +
                    "HAVING counts >= 3) as d)"; // SQL查询语句
            resultSet = statement.executeQuery(sql);

            // 5. 处理结果集并转换为User对象
            while (resultSet.next()) {
                Article blog = new Article();
                blog.setOId(resultSet.getString("oId"));
                blog.setArticleTitle(resultSet.getString("articleTitle"));
                blog.setArticleAbstract(resultSet.getString("articleAbstract"));
                blog.setArticleAbstractText(resultSet.getString("articleAbstractText"));
                blog.setArticleTags(resultSet.getString("articleTags"));
                blog.setArticleAuthorId(resultSet.getString("articleAuthorId"));
                blog.setArticleContent(resultSet.getString("articleContent"));
                blog.setArticlePermalink(resultSet.getString("articlePermalink"));
                blog.setArticlePutTop(resultSet.getString("articlePutTop"));
                blog.setArticleCreated(resultSet.getLong("articleCreated"));
                blog.setArticleUpdated(resultSet.getLong("articleUpdated"));
                blog.setArticleRandomDouble(resultSet.getString("articleRandomDouble"));
                blog.setArticleSignId(resultSet.getString("articleSignId"));
                blog.setArticleViewPwd(resultSet.getString("articleViewPwd"));
                blog.setArticleImg1URL(resultSet.getString("articleImg1URL"));
                blog.setArticleStatus(resultSet.getInt("articleStatus"));
                blogs.add(blog);
            }
            System.err.println("总行数："+blogs.size());
            Map<String, ArticleSeries> articleSeries = new HashMap<>();
            for(Article article : blogs ) {
                articleSeries.computeIfAbsent(article.getFirstTag(), tag-> new ArticleSeries(article.getFirstTag(), StrFormatter.format("/newcloud/blog-document/blog{}", sql.contains("not in")?"/待分组":""))).stack(article);
            }
            articleSeries.values().parallelStream().forEach(series->{
                // 存储 系列 分组 文章
                series.store();
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7. 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
//    private static String writeMarkdown(Article blog, boolean notInFlag) {
//        String basePath = StrFormatter.format("/newcloud/blog-document/blog{}",notInFlag?"/待分组":"");
//        String series = blog.getArticleTags();
//        if( series.contains(",") ) {
//            series = Splitter.on(",").splitToList(series).get(0);
//        }
//        String blogId = blog.getOId();
//        String realPath = StrFormatter.format("{}/{}/{}.md",basePath, series, blogId);
//        File markdownFile = new File(realPath);
//        try {
//            System.out.println(realPath+" created!");
//            markdownFile.getParentFile().mkdirs();
//            Files.write(markdownFile.toPath(), replaceImg(blog.getArticleContent()).getBytes(StandardCharsets.UTF_8) );
//        } catch (IOException e) {
//        }
//        return realPath;
//    }

}

@Data
class Article {
    private String oId;
    private String articleTitle;
    private String articleAbstract;
    private String articleAbstractText;
    private String articleTags;
    private String articleAuthorId;
    private String articleContent;
    private String articlePermalink;
    private String articlePutTop;
    private Long articleCreated;
    private Long articleUpdated;
    private String articleRandomDouble;
    private String articleSignId;
    private String articleViewPwd;
    private String articleImg1URL;
    private int articleStatus;

    public String getFirstTag() {
        if( !StringUtil.isEmpty(articleTags) && articleTags.contains(",") ) {
            return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(articleTags).get(0);
        }
        return articleTags;
    }
}

class ArticleSeries {

    public ArticleSeries(String topicName, String basePath) {
        this.basePath = basePath;
        this.topicName = topicName;
    }
    private String basePath;
    /**
     * 是否添加关注
     */
    private String topicName;
    private Map</* blog.oId */String, BlogInfo> blogs = new HashMap<>();

    public void stack(Article article) {
        blogs.put(article.getOId(), new BlogInfo(article.getArticleContent(), article));
    }

    /**
     * 将 博客系统 文件写入磁盘
     */
    public void store(){
        // 遍历写入文档
        StringBuffer seriesBuffer = new StringBuffer(StrFormatter.format("# {}\n",topicName));
        blogs.keySet().stream().sorted().forEach(oId->{
            BlogInfo blog = blogs.get(oId);
            String writeFilePath = StrFormatter.format("{}/{}/{}.md",basePath, topicName,oId);
            writeMarkdown(writeFilePath, blog.toString());
            seriesBuffer.append(StrFormatter.format("## [{}]({})\n", blog.getArticle().getArticleTitle(), createLink(oId)));
        });
        String writeSeriesFilePath = StrFormatter.format("{}/{}/README.md", basePath, topicName);
        // 写入系列文章的 README 文件
        writeMarkdown(writeSeriesFilePath, seriesBuffer.toString());
    }

    private String createLink(String linkId) {
        return StrFormatter.format("./{}.md", linkId);
    }

    private String writeMarkdown(String realPath, String content) {
        File markdownFile = new File(realPath);
        try {
            System.out.println(realPath+" created!");
            markdownFile.getParentFile().mkdirs();
            Files.write(markdownFile.toPath(), replaceImg(content).getBytes(StandardCharsets.UTF_8) );
        } catch (IOException e) {
        }
        return realPath;
    }

    private static String replaceImg(String articleContent) {
        // 转换 gitee 上 图床中的图片
        return articleContent.replaceAll("https://caoyangjie.gitee.io/","https://gitbook.luckycxy.com/");
    }
}

@Data
class BlogInfo {
    public BlogInfo(String content, Article article) {
        this.content = content;
        this.article = article;
    }

    private Article article;
    private String content;
    private String prefixContent;
    private String affixContent;

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(!StringUtil.isEmpty(prefixContent)?prefixContent.concat("\n"):"");
        buffer.append(content);
        if( !StringUtil.isEmpty(affixContent) ) {
            buffer.append("\n");
            buffer.append(affixContent);
        }
        return buffer.toString();
    }
}
