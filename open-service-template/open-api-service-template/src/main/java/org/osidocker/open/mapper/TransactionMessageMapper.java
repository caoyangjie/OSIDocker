package org.osidocker.open.mapper;

import java.util.Map;

import org.osidocker.open.entity.RpTransactionMessage;

import com.github.pagehelper.Page;

public interface TransactionMessageMapper {

	int insert(RpTransactionMessage message);

	void update(RpTransactionMessage message);

	RpTransactionMessage getMessageBy(Map<String, Object> paramMap);

	void delete(Map<String, Object> paramMap);

	Page<RpTransactionMessage> listPage(Map<String, Object> paramMap);

}
