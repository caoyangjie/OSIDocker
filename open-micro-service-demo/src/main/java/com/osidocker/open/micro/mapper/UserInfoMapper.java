package com.osidocker.open.micro.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;


public interface UserInfoMapper {
	public Map<String,Object> findByUsername(@Param("username") String username);
}
