package com.gpvision.utils;

public class Message {
	private final int what;
	private final Object object;

	public Message(int what) {
		this.what = what;
		this.object = null;
	}

	public Message(int what, Object object) {
		super();
		this.what = what;
		this.object = object;
	}

	public int getWhat() {
		return what;
	}

	public Object getObject() {
		return object;
	}

}
