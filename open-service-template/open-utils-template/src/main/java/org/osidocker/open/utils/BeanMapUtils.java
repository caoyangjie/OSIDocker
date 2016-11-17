package org.osidocker.open.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

public class BeanMapUtils {
    // Map --> Bean 2: 利用org.apache.commons.beanutils 工具类实现 Map --> Bean  
    public static <T> T transMap2Bean(Map<String, Object> map, Class<T> t) {
        if (map == null || t == null) {
            return null;  
        }
        try {  
        	T instance = t.newInstance();
            BeanUtils.populate(instance, map);  
            return instance;
        } catch (Exception e) {  
            System.out.println("transMap2Bean2 Error " + e);  
        }  
        return null;
    }  
    
    public static <T> List<T> transListMap2Bean(List<Map<String,Object>> list, Class<T> t){
    	List<T> l = new ArrayList<T>();
    	for (Map<String, Object> map : list) {
			l.add(transMap2Bean(map, t));
		}
    	return l;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> PageInfo<T> transListMap2Bean(Page<Map<String,Object>> list, Class<T> t){
    	List<T> l = new ArrayList<T>();
    	for (Map<String, Object> map : list) {
    		l.add(transMap2Bean(map, t));
    	}
    	Page p = new Page<>();
    	p.addAll(l);
    	return new PageInfo<T>(p);
    }
    
//    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean  
//    public static <T> T transMap2Bean(Map<String, Object> map, Class<T> t) {  
//  
//        try {  
//        	T instance = t.newInstance();
//            BeanInfo beanInfo = Introspector.getBeanInfo(t);  
//            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
//  
//            for (PropertyDescriptor property : propertyDescriptors) {  
//                String key = property.getName();  
//  
//                if (map.containsKey(key)) {  
//                    Object value = map.get(key);  
//                    // 得到property对应的setter方法  
//                    Method setter = property.getWriteMethod();  
//                    setter.invoke(instance, value);  
//                }  
//  
//            }  
//            return instance;
//        } catch (Exception e) {  
//            System.out.println("transMap2Bean Error " + e);  
//        }  
//  
//        return null;  
//  
//    }  
  
    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map  
    public static Map<String, Object> transBean2Map(Object obj) {  
  
        if(obj == null){  
            return null;  
        }          
        Map<String, Object> map = new HashMap<String, Object>();  
        try {  
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
            for (PropertyDescriptor property : propertyDescriptors) {  
                String key = property.getName();  
  
                // 过滤class属性  
                if (!key.equals("class")) {  
                    // 得到property对应的getter方法  
                    Method getter = property.getReadMethod();  
                    Object value = getter.invoke(obj);  
  
                    map.put(key, value);  
                }  
  
            }  
        } catch (Exception e) {  
            System.out.println("transBean2Map Error " + e);  
        }  
  
        return map;  
  
    }  
}
