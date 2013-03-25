package com.gpvision.utils;

public class Message {
	private final int what;
	private final Object object;
	private final String tag;

	public Message(int what) {
		this.what = what;
		this.object = null;
		this.tag = null;
	}

	public Message(int what, Object object) {
		super();
		this.what = what;
		this.object = object;
		this.tag = null;
	}

	public Message(int what, Object object, String tag) {
		super();
		this.what = what;
		this.object = object;
		this.tag = tag;
	}

	public int getWhat() {
		return what;
	}

	public Object getObject() {
		return object;
	}

	public String getTag() {
		return tag;
	}

}
