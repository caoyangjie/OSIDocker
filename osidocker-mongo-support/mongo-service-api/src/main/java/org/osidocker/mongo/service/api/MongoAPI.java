package org.osidocker.mongo.service.api;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public interface MongoAPI {
	/**
	 * 通过条件查询,查询分页结果
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param query
	 * @return
	 */
//	public Pagination<T> getPage(int pageNo, int pageSize, Query query);

	/**
	 * 通过条件查询实体(集合)
	 * 
	 * @param query
	 */
	public <T> List<T> find(Query query,Class<T> t);

	/**
	 * 通过一定的条件查询一个实体
	 * 
	 * @param query
	 * @return
	 */
	public <T> T findOne(Query query,Class<T> t);

	/**
	 * 查询出所有数据
	 * 
	 * @return
	 */
	public <T> List<T> findAll(Class<T> t);

	/**
	 * 查询并且修改记录
	 * 
	 * @param query
	 * @param update
	 * @return
	 */
	public <T> T findAndModify(Query query, Update update,Class<T> t);

	/**
	 * 按条件查询,并且删除记录
	 * 
	 * @param query
	 * @return
	 */
	public <T> T findAndRemove(Query query,Class<T> t);

	/**
	 * 通过条件查询更新数据
	 * 
	 * @param query
	 * @param update
	 * @return
	 */
	public <T> void updateFirst(Query query, Update update,Class<T> t);

	/**
	 * 保存一个对象到mongodb
	 * 
	 * @param bean
	 * @return
	 */
	public <T> T save(T bean);

	/**
	 * 通过ID获取记录
	 * 
	 * @param id
	 * @return
	 */
	public <T> T findById(String id,Class<T> t);

	/**
	 * 通过ID获取记录,并且指定了集合名(表的意思)
	 * 
	 * @param id
	 * @param collectionName
	 *            集合名
	 * @return
	 */
	public <T> T findById(String id, String collectionName,Class<T> t);
}
