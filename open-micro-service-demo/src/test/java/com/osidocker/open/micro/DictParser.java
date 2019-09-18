package com.osidocker.open.micro;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 * @creato 2019-08-21 10:43
 */
public class DictParser {
//    public static String POST_URI = "https://www.szeiv.com/public/getDict";
    public static String POST_URI = "https://www.szeiv.com/public/getSubLocation";
//    public static String POST_URI = "https://www.szeiv.com/public/getConfigForApp";
//    public static String POST_URI = "https://www.szeiv.com/public/updateVersion?versionCode=12";
//    public static String POST_URI = "https://www.szeiv.com/organization/getDepartments";

    public static void main(String[] args){
//        parserDictSql();
        parserLocationSql("110114");
    }

    private static void parserLocationSql(String locationId) {
        POST_URI = "https://www.szeiv.com/public/getSubLocation";
        Map data=new HashMap<>();
        data.put("parentId",locationId);
        JSONObject json = JSONObject.parseObject(HttpClientRequests.sendPostRequest(POST_URI,data));
        Set<String> locationSet = SqlParser.parserSubLocationJson(json.toJSONString());
        Set<String> locationSets = new HashSet<>();
        Set<String> locationSets1 = new HashSet<>();
        for (String lid : locationSet){
            data.put("parentId",lid);
            locationSets.addAll(SqlParser.parserSubLocationJson(HttpClientRequests.sendPostRequest(POST_URI,data)));
        }
//        for (String lid : locationSets){
//            data.put("parentId",lid);
//            locationSets1.addAll(SqlParser.parserSubLocationJson(HttpClientRequests.sendPostRequest(POST_URI,data)));
//        }
    }

    private static Set<String> initDictKeys() {
        return Stream.of(
                "YiDiTime","ChannelStatus","PropertyTypeRate","Landspace","NearRoad","HouseType","Forward","Equity",
                "BusiType","penaltyInterestRate","ProjectOrg","CountType","SettleType","CountBase","AcctStatus","ReceiverOrg",
                "OpenCity","Extension","LoanType","Term","Currency","ProductRepayType","InterestType","Grance","RepayDateType",
                "RepayDateCategory","RateType","RateSumType","FeeSubjectType","ComputeBase","SubjectSumType","FeeType","fixedRate",
                "PriorityType","AcctOrg","AcctType","AcctOwnerType","AcctOwnerName","AcctStatus","penaltyInterestRate","LendResult",
                "ZhRepayment","CompensatoryType","CtrditorType","EarlyRepayType","RepayResult","RepayBusiSource","ReductionStatus",
                "ReductionType","penaltyInterestRate","status","ExtPaymentType","ExtApplyStatus","RepayType"
        ).collect(Collectors.toSet());
    }

    private static void parserDictSql() {
        Set<String> set = initDictKeys();
//        set = new HashSet<>();
        JSONObject json = JSONObject.parseObject(HttpClientRequests.sendPostRequest(POST_URI,set,null));
        System.out.println(json);
        json.getJSONObject("data").values().forEach(jsonArr->{
            JSONArray arr = (JSONArray) jsonArr;
            arr.forEach(jsonVal->{
                JSONObject data = (JSONObject) jsonVal;
                Long dictId = ((JSONObject) jsonVal).getLong("dictId");
                String type = ((JSONObject) jsonVal).getString("type");
                String value = ((JSONObject) jsonVal).getString("value");
                String name = ((JSONObject) jsonVal).getString("name");
                String note = ((JSONObject) jsonVal).getString("note");
                Long parentId = ((JSONObject) jsonVal).getLong("parentId");
                System.out.println(
                        String.format("INSERT INTO `dict`(`dict_id`, `type`, `value`, `name`, `note`, `parent_id`) VALUES (%s, '%s', '%s', '%s', '%s', %s);",
                        dictId,type,value,name,note,parentId));
            });
        });
    }
}
