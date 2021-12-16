package com.osidocker.open.micro.utils;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenAppNamespaceDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import com.google.common.base.Splitter;
import com.osidocker.open.micro.annotation.ApolloIgnoreField;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * apollo 配置中心
 * 发布与获取组件
 */
@Component
public class ApolloUtil {

	@Autowired
	private ApolloOpenApiClient apolloClient;

	@Autowired
	private ApolloOpen apolloOpen;

	/**
	 * 删除 namespace 中的 keys
	 * @param aid
	 * @param namespace
	 * @param removeItems
	 */
	public void remove(String aid, String namespace, String prefix, List removeItems) {
		String appId = StringUtil.isEmpty(aid)?apolloOpen.getAppId():aid;
		removeItems.stream().forEach(data->{
			getBeanPath(data, "", new ArrayList<>()).stream().forEach(key->{
				try{
					apolloClient.removeItem(appId, apolloOpen.getEnv(), apolloOpen.getClusterName(), namespace, prefix.concat(".").concat(key), "apollo");
				} catch(Exception e){
				}
			});
		});
		NamespaceReleaseDTO releaseDTO = new NamespaceReleaseDTO();
		releaseDTO.setReleasedBy(apolloOpen.getReleaseAuthor());
		releaseDTO.setReleaseTitle("release job");
		apolloClient.publishNamespace(appId, apolloOpen.getEnv(), apolloOpen.getClusterName(), namespace, releaseDTO);
	}

	/**
	 * 获取 appid
	 * @param aid
	 * @return
	 */
	public boolean checkAppId(String aid){
		String appId = StringUtil.isEmpty(aid)?apolloOpen.getAppId():aid;
		return !CollectionUtils.isEmpty(apolloClient.getAppsByIds(Arrays.asList(appId)));
	}

	/**
	 * 发布 对象 到 apollo
	 * @param aid
	 * @param namespace
	 * @param prefix
	 * @param releaseData
	 */
	public void publish(String aid, String namespace, String prefix, Object releaseData, boolean isPublic){
		String appId = StringUtil.isEmpty(aid)?apolloOpen.getAppId():aid;
		checkNamespace(appId, namespace, isPublic);
		getBeanPath(releaseData, "", new ArrayList<>()).forEach(beanPath->{
			String publishValue = Optional.ofNullable(new BeanPath(beanPath).get(releaseData)).orElse("").toString();
			OpenItemDTO item = new OpenItemDTO();
			item.setKey(prefix.concat(".").concat(beanPath));
			item.setValue(publishValue);
			item.setDataChangeCreatedBy(apolloOpen.getReleaseAuthor());
			apolloClient.createOrUpdateItem(appId,
					apolloOpen.getEnv(),
					apolloOpen.getClusterName(),
					namespace,
					item);
		});
		NamespaceReleaseDTO releaseDTO = new NamespaceReleaseDTO();
		releaseDTO.setReleasedBy(apolloOpen.getReleaseAuthor());
		releaseDTO.setReleaseTitle("release job");
		apolloClient.publishNamespace(appId, apolloOpen.getEnv(), apolloOpen.getClusterName(), namespace, releaseDTO);
	}

	private void checkNamespace(String aid, String namespace, boolean isPublic){
		String appId = StringUtil.isEmpty(aid)?apolloOpen.getAppId():aid;
		try {
			apolloClient.getNamespace(appId, apolloOpen.getEnv(), apolloOpen.getClusterName(), namespace);
		}catch (RuntimeException e){
			OpenAppNamespaceDTO namespaceDTO = new OpenAppNamespaceDTO();
			namespaceDTO.setAppId(appId);
			namespaceDTO.setName(namespace);
			namespaceDTO.setAppendNamespacePrefix(false);
			namespaceDTO.setPublic(isPublic);
			namespaceDTO.setDataChangeCreatedBy(apolloOpen.getReleaseAuthor());
			namespaceDTO.setDataChangeCreatedTime(new Date());
			try{
				apolloClient.createAppNamespace(namespaceDTO);
			} catch(Exception e2){
			    throw e2;
			}
		}
	}

	/**
	 * 获取 命名空间中的 key 转换 为 对象
	 * @param aid
	 * @param namespace
	 * @param prefix
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public <T> Map<String, T> getReleaseData(String aid, String namespace, String prefix, Class<T> clazz) {
		String appId = StringUtil.isEmpty(aid)?apolloOpen.getAppId():aid;
		Map<String, T> releaseMap = new HashMap<>();
		OpenNamespaceDTO namespaceDTO = apolloClient.getNamespace(appId, apolloOpen.getEnv(), apolloOpen.getClusterName(), namespace);
		String subNames = "";
		namespaceDTO.getItems().stream().filter(item->item.getKey().equalsIgnoreCase(prefix.concat(".").concat(""))).findFirst().ifPresent(item->{
			subNames.concat(item.getValue());
		});
		List<String> subNameList = Splitter.on(",").splitToList(subNames).stream().map(key-> prefix.concat(".").concat(key) ).collect(Collectors.toList());
		namespaceDTO.getItems().stream().filter(item-> {
		    return subNameList.stream().filter(hasKeyPrefix->item.getKey().startsWith(hasKeyPrefix)).findAny().isPresent();
		}).forEach(item->{
			String subName = item.getKey().substring(item.getKey().lastIndexOf("."));
			T instance = releaseMap.computeIfAbsent(subName, sn-> {
				return fillBeanWithDefault(new BeanWrapperImpl(clazz));
			});
			BeanPath.create(item.getKey().replace(prefix.concat(".").concat(subName),"")).set(instance, item.getValue());
		});
		return releaseMap;
	}

	/**
	 * 读取 apollo 配置中心 指定前缀key 的命名对象
	 * @param aid
	 * @param namespace
	 * @param prefix
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public <T> T getReleaseObject(String aid, String namespace, String prefix, Class<T> clazz) {
		String appId = StringUtil.isEmpty(aid)?apolloOpen.getAppId():aid;
		OpenNamespaceDTO namespaceDTO = apolloClient.getNamespace(appId, apolloOpen.getEnv(), apolloOpen.getClusterName(), namespace);
		BeanWrapperImpl instanceWrapper = new BeanWrapperImpl(clazz);
		T releaseData = fillBeanWithDefault(instanceWrapper);
		namespaceDTO.getItems().stream().filter(item-> item.getKey().startsWith(prefix)).forEach(item->{
			BeanPath.create(item.getKey().replace(prefix.concat("."),"")).set(releaseData, item.getValue());
		});
		return releaseData;
	}

	private <T> T fillBeanWithDefault(BeanWrapperImpl instanceWrapper){
		for (PropertyDescriptor pd : instanceWrapper.getPropertyDescriptors()) {
			// 处理非 简单对象字段 默认为 null, 而 BeanPath 实例去进行 path 设值时 字段值不允许为空
			if( !ClassUtil.isSimpleValueType(pd.getPropertyType()) ){
				instanceWrapper.setPropertyValue(pd.getName(), BeanUtils.instantiateClass(pd.getPropertyType()));
			}
		}
		return (T) instanceWrapper.getRootInstance();
	}

	/**
	 * 获取 给定对象 所有非空属性,并生成对应的 beanPath 字符串
	 * @param data
	 * @param basePath
	 * @param beanPaths
	 * @return
	 */
	public static List<String> getBeanPath(Object data, String basePath, List<String> beanPaths){
		basePath = StringUtil.isEmpty(basePath)?"":(basePath+".");
		BeanWrapperImpl bwi = new BeanWrapperImpl(data);
		BeanDesc desc = new BeanDesc(data.getClass());
		for(PropertyDescriptor pd : bwi.getPropertyDescriptors() ){
			Object propertyValue = bwi.getPropertyValue(pd.getName());
			if( ObjectUtil.isNotEmpty(propertyValue) && pd.getWriteMethod()!=null ){
				Field field = desc.getField(pd.getName());
				if( field.isAnnotationPresent(ApolloIgnoreField.class) ){
					continue;
				}
				if(ClassUtil.isSimpleValueType(pd.getPropertyType())){
					beanPaths.add(basePath+pd.getName());
				}else if( Map.class.isAssignableFrom(pd.getPropertyType()) ) {
					Map mapValue = (Map) bwi.getPropertyValue(pd.getName());
					String finalBasePath = basePath;
					mapValue.forEach((k, v)->{
						if( ClassUtil.isSimpleValueType(v.getClass()) ){
							beanPaths.add(finalBasePath+pd.getName()+"."+k);
						}else{
							beanPaths.addAll(getBeanPath(v, finalBasePath +pd.getName()+"."+k, new ArrayList<>()));
						}
					});
				}else if( Collection.class.isAssignableFrom(pd.getPropertyType()) ){
					Collection collectValue = (Collection) bwi.getPropertyValue(pd.getName());
					List listValue = Arrays.asList(collectValue.toArray());
					for(int i = 0; i < listValue.size(); i ++){
						Object val = listValue.get(i);
						if( ClassUtil.isSimpleValueType(val.getClass()) ){
							beanPaths.add(basePath+pd.getName()+String.format("[%s]",i));
						}else{
							beanPaths.addAll(getBeanPath(val, basePath+pd.getName()+String.format("[%s]",i), new ArrayList<>()));
						}
					}
				}else{
					beanPaths.addAll(getBeanPath(propertyValue, basePath+pd.getName(), new ArrayList<>()));
				}
			}
		}
		return beanPaths;
	}
}
