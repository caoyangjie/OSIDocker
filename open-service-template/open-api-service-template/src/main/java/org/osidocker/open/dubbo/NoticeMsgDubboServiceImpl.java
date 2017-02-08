package org.osidocker.open.dubbo;

import org.osidocker.open.api.NoticeMsgService;
import org.osidocker.open.entity.RpTransactionMessage;
import org.osidocker.open.exceptions.MessageException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class NoticeMsgDubboServiceImpl implements NoticeMsgService {
	
	@Autowired
	protected NoticeMsgService noticeService;

	@Override
	public int saveMessageWaitingConfirm(RpTransactionMessage rpTransactionMessage) throws MessageException {
		return noticeService.saveMessageWaitingConfirm(rpTransactionMessage);
	}

	@Override
	public void confirmAndSendMessage(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.confirmAndSendMessage(messageId);
	}

	@Override
	public int saveAndSendMessage(RpTransactionMessage rpTransactionMessage) throws MessageException {
		// TODO Auto-generated method stub
		return noticeService.saveAndSendMessage(rpTransactionMessage);
	}

	@Override
	public void directSendMessage(RpTransactionMessage rpTransactionMessage) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.directSendMessage(rpTransactionMessage);
	}

	@Override
	public void reSendMessage(RpTransactionMessage rpTransactionMessage) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.reSendMessage(rpTransactionMessage);
	}

	@Override
	public void reSendMessageByMessageId(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.reSendMessageByMessageId(messageId);
	}

	@Override
	public void setMessageToAreadlyDead(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.setMessageToAreadlyDead(messageId);
	}

	@Override
	public RpTransactionMessage getMessageByMessageId(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		return noticeService.getMessageByMessageId(messageId);
	}

	@Override
	public void deleteMessageByMessageId(String messageId) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.deleteMessageByMessageId(messageId);
	}

	@Override
	public void reSendAllDeadMessageByQueueName(String queueName, int batchSize) throws MessageException {
		// TODO Auto-generated method stub
		noticeService.reSendAllDeadMessageByQueueName(queueName, batchSize);
	}

}
