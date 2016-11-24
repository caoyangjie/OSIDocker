package org.osidocker.open.callback;

import java.util.HashMap;

public interface SmsNotificationBackHandler {
	void succHandler(HashMap<String, Object> data);
	void failHandler(HashMap<String, Object> data);
	void errorHandler(Exception e);
	void doneHandler(HashMap<String, Object> data);
}
