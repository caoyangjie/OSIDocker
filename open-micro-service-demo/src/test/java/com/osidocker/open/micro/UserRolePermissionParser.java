package com.osidocker.open.micro;

import com.alibaba.fastjson.JSONObject;

import java.util.HashSet;

/**
 * @author Administrator
 * @creato 2019-08-28 21:11
 */
public class UserRolePermissionParser {

    public static void main(String[] args){
        String postRpUrl = "https://www.szeiv.com/permission/findPermissionTree";
        String postRoleUrl = "https://www.szeiv.com/role/findRoles";
        JSONObject json = new JSONObject();
        json.put("city","");
        json.put("name","");
        json.put("order","asc");
        String jsonData = HttpClientRequests.sendPostRequest(postRoleUrl,new HashSet<>(),json);
        System.out.println(jsonData);
    }
}
