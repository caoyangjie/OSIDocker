package org.osidocker.open.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.osidocker.open.api.NoticeMsgService;
import org.osidocker.open.entity.RpTransactionMessage;
import org.osidocker.open.enums.MessageStatusEnum;
import org.osidocker.open.enums.PublicEnum;
import org.osidocker.open.exceptions.MessageException;
import org.osidocker.open.mapper.TransactionMessageMapper;
import org.osidocker.open.utils.PublicConfigUtil;
import org.osidocker.open.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class NoticeMsgServiceImpl extends OsiDockerBaseService implements NoticeMsgService {
	
	@Autowired
	protected TransactionMessageMapper tmMapper;
	
//	@Autowired
//	private JmsTemplate notifyJmsTemplate;

	@Override
	public int saveMessageWaitingConfirm(RpTransactionMessage message) throws MessageException {
		// TODO Auto-generated method stub
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}

		if (StringUtil.isEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, messageServ.getMessage(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL+""));
		}
		
		message.setEditTime(new Date());
		message.setStatus(MessageStatusEnum.WAITING_CONFIRM.name());
		message.setAreadlyDead(PublicEnum.NO.name());
		message.setMessageSendTimes(0);
		return tmMapper.insert(message);
	}

	@Override
	public void confirmAndSendMessage(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		final RpTransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}
		
		message.setStatus(MessageStatusEnum.SENDING.name());
		message.setEditTime(new Date());
		tmMapper.update(message);
		
//		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
//		notifyJmsTemplate.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage(message.getMessageBody());
//			}
//		});
	}

	@Override
	public int saveAndSendMessage(RpTransactionMessage message) throws MessageException {
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}

		if (StringUtil.isEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, messageServ.getMessage(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL+""));
		}

		message.setStatus(MessageStatusEnum.SENDING.name());
		message.setAreadlyDead(PublicEnum.NO.name());
		message.setMessageSendTimes(0);
		message.setEditTime(new Date());
		int result = tmMapper.insert(message);

//		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
//		notifyJmsTemplate.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage(message.getMessageBody());
//			}
//		});
		
		return result;
	}

	@Override
	public void directSendMessage(RpTransactionMessage message) throws MessageException {
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}

		if (StringUtil.isEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, messageServ.getMessage(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL+""));
		}

//		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
//		notifyJmsTemplate.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage(message.getMessageBody());
//			}
//		});
	}

	@Override
	public void reSendMessage(RpTransactionMessage message) throws MessageException {
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}

		if (StringUtil.isEmpty(message.getConsumerQueue())) {
			throw new MessageException(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL, messageServ.getMessage(MessageException.MESSAGE_CONSUMER_QUEUE_IS_NULL+""));
		}
		
		message.addSendTimes();
		message.setEditTime(new Date());
		tmMapper.update(message);

//		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
//		notifyJmsTemplate.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage(message.getMessageBody());
//			}
//		});
	}

	@Override
	public void reSendMessageByMessageId(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		final RpTransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}
		
		int maxTimes = Integer.valueOf(PublicConfigUtil.readConfig("message.max.send.times"));
		if (message.getMessageSendTimes() >= maxTimes) {
			message.setAreadlyDead(PublicEnum.YES.name());
		}
		
		message.setEditTime(new Date());
		message.setMessageSendTimes(message.getMessageSendTimes() + 1);
		tmMapper.update(message);
		
//		notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
//		notifyJmsTemplate.send(new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage(message.getMessageBody());
//			}
//		});
	}

	@Override
	public void setMessageToAreadlyDead(String messageId) throws MessageException {
		RpTransactionMessage message = getMessageByMessageId(messageId);
		if (message == null) {
			throw new MessageException(MessageException.SAVA_MESSAGE_IS_NULL, messageServ.getMessage(MessageException.SAVA_MESSAGE_IS_NULL+""));
		}
		
		message.setAreadlyDead(PublicEnum.YES.name());
		message.setEditTime(new Date());
		tmMapper.update(message);
	}

	@Override
	public RpTransactionMessage getMessageByMessageId(String messageId) throws MessageException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("messageId", messageId);
		return tmMapper.getMessageBy(paramMap);
	}

	@Override
	public void deleteMessageByMessageId(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("messageId", messageId);
		tmMapper.delete(paramMap);
	}

	@Override
	public void reSendAllDeadMessageByQueueName(String queueName, int batchSize) throws MessageException {
		log.info("==>reSendAllDeadMessageByQueueName");
		
		int numPerPage = 1000;
		if (batchSize > 0 && batchSize < 100){
			numPerPage = 100;
		} else if (batchSize > 100 && batchSize < 5000){
			numPerPage = batchSize;
		} else if (batchSize > 5000){
			numPerPage = 5000;
		} else {
			numPerPage = 1000;
		}
		
		int pageNum = 1;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("consumerQueue", queueName);
		paramMap.put("areadlyDead", PublicEnum.YES.name());
		paramMap.put("listPageSortType", "ASC");
		
		PageHelper.startPage(numPerPage, 1);
		Page<RpTransactionMessage> pageData = tmMapper.listPage(paramMap);
		
		if (pageData == null || pageData.isEmpty()) {
			log.info("==>recordList is empty");
			return;
		}
		for (final RpTransactionMessage message : pageData) {
			message.setEditTime(new Date());
			message.addSendTimes();
			
			tmMapper.update(message);
			
//			notifyJmsTemplate.setDefaultDestinationName(message.getConsumerQueue());
//			notifyJmsTemplate.send(new MessageCreator() {
//				public Message createMessage(Session session) throws JMSException {
//					return session.createTextMessage(message.getMessageBody());
//				}
//			});
		}
	}

}
