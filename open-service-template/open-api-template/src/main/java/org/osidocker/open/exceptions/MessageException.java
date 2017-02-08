package org.osidocker.open.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageException extends OsidockerException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3536909333010163563L;

	/** 保存的消息为空 **/
	public static final int SAVA_MESSAGE_IS_NULL = 8001;
	
	/** 消息的消费队列为空 **/
	public static final int MESSAGE_CONSUMER_QUEUE_IS_NULL = 8002;

//	private static final Log LOG = LogFactory.getLog(MessageException.class);
	protected Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

	public MessageException() {
	}

	public MessageException(int code, String msgFormat, Object... args) {
		super(code, msgFormat, args);
	}

	public MessageException(int code, String msg) {
		super(code, msg);
	}

	public MessageException print() {
		LOG.info("==>BizException, code:" + this.code + ", msg:" + this.msg);
		return this;
	}
}
