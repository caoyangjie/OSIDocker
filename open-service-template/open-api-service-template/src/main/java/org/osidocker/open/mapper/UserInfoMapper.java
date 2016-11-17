package org.osidocker.open.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;

public interface UserInfoMapper {
	public Map<String,Object> findByUsername(@Param("username")String username);
	
	public Page<Map<String,Object>> pageList(@Param("username")String username);
}
