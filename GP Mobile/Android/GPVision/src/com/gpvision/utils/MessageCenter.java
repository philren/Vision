package com.gpvision.utils;

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;

public class MessageCenter {
	private static final MessageCenter instance = new MessageCenter();
	private final Map<Integer, MessageListener> listenerQueue = new HashMap<Integer, MessageListener>();

	private MessageCenter() {
	}

	public static MessageCenter getInstance() {
		return instance;
	}

	public void addMessageListener(int what, MessageListener listener) {
		if (!listenerQueue.containsKey(what)) {
			listenerQueue.put(what, listener);
		}
	}

	public void removeMessageListener(int what) {
		if (listenerQueue.containsKey(what)) {
			listenerQueue.remove(what);
		}
	}

	public void sendMessage(final Message message) {
		if (listenerQueue.containsKey(message.getWhat())) {
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					MessageListener listener = listenerQueue.get(message
							.getWhat());
					listener.onMessageReceived(message);
				}
			});
		}
	}

	public interface MessageListener {
		void onMessageReceived(Message message);
	}
}
