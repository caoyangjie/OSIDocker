package org.osidocker.open.api;

import org.osidocker.open.entity.RpTransactionMessage;
import org.osidocker.open.exceptions.MessageException;

/**
 * 最终一致性消息服务
 * @author Administrator
 *
 */
public interface NoticeMsgService {
	/**
	 * 预存储消息. 
	 */
	public int saveMessageWaitingConfirm(RpTransactionMessage rpTransactionMessage) throws MessageException;
	
	/**
	 * 确认并发送消息.
	 */
	public void confirmAndSendMessage(String messageId) throws MessageException;

	/**
	 * 存储并发送消息.
	 */
	public int saveAndSendMessage(RpTransactionMessage rpTransactionMessage) throws MessageException;

	/**
	 * 直接发送消息.
	 */
	public void directSendMessage(RpTransactionMessage rpTransactionMessage) throws MessageException;
	
	/**
	 * 重发消息.
	 */
	public void reSendMessage(RpTransactionMessage rpTransactionMessage) throws MessageException;
	
	/**
	 * 根据messageId重发某条消息.
	 */
	public void reSendMessageByMessageId(String messageId) throws MessageException;
	
	/**
	 * 将消息标记为死亡消息.
	 */
	public void setMessageToAreadlyDead(String messageId) throws MessageException;


	/**
	 * 根据消息ID获取消息
	 */
	public RpTransactionMessage getMessageByMessageId(String messageId) throws MessageException;

	/**
	 * 根据消息ID删除消息
	 */
	public void deleteMessageByMessageId(String messageId) throws MessageException;
	
	/**
	 * 重发某个消息队列中的全部已死亡的消息.
	 */
	public void reSendAllDeadMessageByQueueName(String queueName, int batchSize) throws MessageException;

}
