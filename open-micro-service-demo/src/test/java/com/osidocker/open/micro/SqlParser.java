package com.osidocker.open.micro;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.osidocker.open.micro.utils.HttpClientUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Administrator
 * @creato 2019-08-20 20:58
 */
public class SqlParser {

    public static void main(String[] args){
        String roleJson = HttpClientUtils.sendGetRequest("http://120.79.229.13/role.json");
        parserRoleJson(roleJson);
        System.out.println("");
        String permissionJson = HttpClientUtils.sendGetRequest("http://120.79.229.13/permission.json");
        parserPermissionJson(permissionJson);
        System.out.println("");
        String subLocation = HttpClientUtils.sendGetRequest("http://120.79.229.13/sublocation.json");
        parserSubLocationJson(subLocation);
        System.out.println("");
        parserRolePermission();
    }
    public static Set<String> parserSubLocationJson(String subLocation) {
        Set<String> locationSet = new HashSet<>();
        JSONObject json = JSONObject.parseObject(subLocation);
        json.getJSONArray("data").forEach(jsonVal->{
            JSONObject data = (JSONObject) jsonVal;
            Long locationId = data.getLong("locationId");
            locationSet.add(locationId+"");
            Long parentId = data.getLong("parentId");
            String name = data.getString("name");
            String shortName = data.getString("shortName");
            String longitude = data.getString("longitude");
            String latitude = data.getString("latitude");
            String level = data.getString("level");
            String sort = data.getString("sort");
            String display = data.getString("display");
            String mailCode = data.getString("mailCode");
            System.out.println(String.format("INSERT INTO `location`(`location_id`, `parent_id`, `name`, `short_name`, `longitude`, `latitude`, `level`, `sort`, `display`, `mail_code`) VALUES " +
                            "(%s, %s, '%s', '%s', %s, %s, %s, %s, %s, %s);",
                        locationId,parentId,name,shortName,longitude,latitude,level,sort,display,mailCode
                    ));
        });
        return locationSet;
    }

    private static void parserRolePermission() {
        set.forEach(id->{
            System.out.println(String.format("INSERT INTO `role_permission`(`role_id`, `permission_id`, `create_time`) VALUES (%s, %s, '%s');",21,id,"2019-08-20 22:19:16"));
        });
    }

    public static void parserRoleJson(String roleJson){
        JSONObject json = JSONObject.parseObject(roleJson);
        json.getJSONObject("data").getJSONArray("content").forEach(jsonVal->{
            printerRoleSQL(jsonVal);
        });
    }

    private static void printerRoleSQL(Object jsonVal) {
        JSONObject data = (JSONObject) jsonVal;
        Long roleId = data.getLongValue("roleId");
        String name = data.getString("name");
        String city = data.getString("city");
        String code = data.getString("code");
        if( code!=null ){
            code = "'"+code+"'";
        }
        String createTime = data.getString("createTime");
        System.out.println(String.format("INSERT INTO `role`(`role_id`, `name`, `code`, `city`, `create_time`) VALUES (%s, '%s', %s, '%s', '%s');",
                roleId,name,code,city,createTime));
    }

    public static void parserPermissionJson(String permissionJson){
        JSONObject json = JSONObject.parseObject(permissionJson);
        json.getJSONArray("data").forEach(jsonVal->{
            printerPermissionSQL(jsonVal);
        });
    }

    private static Set<Long> set = new HashSet<>();
    private static void printerPermissionSQL(Object jsonVal) {
        JSONObject data = (JSONObject) jsonVal;
        Long id = data.getLong("permissionId");
        set.add(id);
        String name = data.getString("name");
        String code = data.getString("code");
        String type = data.getString("type");
        String content = data.getString("content");
        if( content!=null ){
            content = "'"+content+"'";
        }
        Long parentId = data.getLong("parentId");
        String createTime = data.getString("createTime");
        System.out.println(String.format("INSERT INTO `permission`(`permission_id`, `name`, `code`, `type`, `content`, `parent_id`, `create_time`) VALUES (%s, '%s', '%s', '%s', %s, %s, '%s');",
                id,name,code,type,content,parentId,createTime));
        JSONArray dataArr = data.getJSONArray("children");
        if( dataArr!=null && dataArr.size() > 0){
            dataArr.forEach(SqlParser::printerPermissionSQL);
        }
    }
}
