package com.osidocker.open.micro.utils;
import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
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
public class ImportHaloMarkdown {
    public static void main(String[] args) {
        HaloQueryWithEntities.generate();
    }
}


class HaloQueryWithEntities {

    private static final String URL = "jdbc:mysql://localhost:3306/halo?useSSL=false"; // 数据库URL
    private static final String USER = "root"; // 数据库用户名
    private static final String PASSWORD = "password"; // 数据库密码

    public static void generate() {
        List<Extensions> exts = new ArrayList<>();
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
            String sql = "select * from extensions"; // SQL查询语句
            resultSet = statement.executeQuery(sql);

            // 5. 处理结果集并转换为User对象
            while (resultSet.next()) {
                Extensions extensions = new Extensions();
                extensions.setName(resultSet.getString("name"));
                extensions.setVersion(resultSet.getString("version"));
                extensions.setDataStr(inputStreamToByteArray(resultSet.getBlob("data").getBinaryStream()));
                exts.add(extensions);
            }
//            System.err.println("总行数："+exts.size());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // 美化后的JSON字符串
//            System.err.println(JSONObject.toJSONString(exts.get(16)));
            for( Extensions ext : exts ) {
                String prettyJson = gson.toJson(gson.fromJson(JSONObject.toJSONString(ext), Object.class));
                if( prettyJson.contains("将网页转换为PDF") ) {
                    System.err.println(StrFormatter.format("{}=>[\n{}\n]",ext.getName(),prettyJson));
                }
            }
            //Map<String, ArticleSeries> articleSeries = new HashMap<>();
//            for(Article article : blogs ) {
//                articleSeries.computeIfAbsent(article.getFirstTag(), tag-> new ArticleSeries(article.getFirstTag(), StrFormatter.format("/newcloud/blog-document/blog{}", sql.contains("not in")?"/待分组":""))).stack(article);
//            }
//            articleSeries.values().parallelStream().forEach(series->{
//                // 存储 系列 分组 文章
//                series.store();
//            });
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

    static String inputStreamToByteArray(InputStream inputStream) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return new String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8")); // 返回 byte 数组
    }
}

@Data
class Extensions {
    private String name;
    private String version;
    private JSONObject data;
    private String dataStr;

    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
        this.data = JSONObject.parseObject(dataStr);
    }
}
