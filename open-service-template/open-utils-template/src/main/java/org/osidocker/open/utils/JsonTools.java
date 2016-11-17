package org.osidocker.open.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/**
 * JSON 工具类
 * 
 * @author Administrator
 *
 */
public class JsonTools
{
    private static final SerializeConfig serializeConfig = new SerializeConfig();
    
    static
    {
        serializeConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        serializeConfig.put(Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
    }
    
    public static String getJsonFromObject(Object req)
    {
        return JSON.toJSONString(req, serializeConfig, SerializerFeature.DisableCircularReferenceDetect);
    }
    
    public static String toJson(Object obj)
    {
        return JSON.toJSONString(obj, serializeConfig, SerializerFeature.DisableCircularReferenceDetect);
    }
    
    public static HashMap<String, Object> obj2Map(Object o){
    	return reflect(parseObject(toJson(o), JSONObject.class));
    }
    
    public static <T> T fromJson(String str, Class<T> clazz)
    {
        return JSON.parseObject(str, clazz);
    }
    
    public static <T> T fromResourceJson(String str, Class<T> clazz)
    {
        return JSON.parseObject(str, clazz);
    }
    
    public static <T> T parseObject(String str, Class<T> clazz)
    {
        return JSON.parseObject(str, clazz);
    }
    
    
    @SuppressWarnings("unchecked")
	public static <T> T parseObject(String json,TypeReference<?> type)
    {	
    	T r = (T) JSON.parseObject(json, type);
        return r;
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T parseRequest(String json,TypeReference<?> type)
    {	
    	T r = (T) JSON.parseObject(json, type);
        return r;
    }
    
	/**
	 * 从map中Object的value中获取java对象
	 * 
	 * @param calzz
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromMapVal(Class<T> calzz,Object obj)
	{
		if(obj instanceof JSONObject)
		{
			JSONObject mm = (JSONObject) obj;
			return JSONObject.toJavaObject(mm, calzz);
		}
		
		 T s = (T)obj;
		return  s;
	}
    
    /**
     * 将JSONObjec对象转换成Map-List集合
     * @see JSONHelper#reflect(JSONArray)
     * @param json
     * @return
     */
    public static HashMap<String, Object> reflect(JSONObject json){
        HashMap<String, Object> map = new HashMap<String, Object>();
        Set<?> keys = json.keySet();
        for(Object key : keys){
            Object o = json.get(key);
            if(o instanceof JSONArray)
                map.put((String) key, reflect((JSONArray) o));
            else if(o instanceof JSONObject)
                map.put((String) key, reflect((JSONObject) o));
            else
                map.put((String) key, o);
        }
        return map;
    }
 
    /**
     * 将JSONArray对象转换成Map-List集合
     * @see JSONHelper#reflect(JSONObject)
     * @param json
     * @return
     */
    public static Object reflect(JSONArray json){
        List<Object> list = new ArrayList<Object>();
        for(Object o : json){
            if(o instanceof JSONArray)
                list.add(reflect((JSONArray) o));
            else if(o instanceof JSONObject)
                list.add(reflect((JSONObject) o));
            else
                list.add(o);
        }
        return list;
    }
}
