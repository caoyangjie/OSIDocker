package org.osidocker.open.callback;

import java.util.List;

import javapns.notification.PushedNotification;

public interface AppleNotificationBackHandler {
	
	void succHandler(List<PushedNotification> list);
	
	void failHandler(List<PushedNotification> list);
	
	void errorHandler(Exception e);
	
	void doneHandler();
}
