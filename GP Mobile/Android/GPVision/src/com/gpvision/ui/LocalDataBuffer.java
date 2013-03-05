package com.gpvision.ui;

public class LocalDataBuffer {
	private static LocalDataBuffer instance = new LocalDataBuffer();

	private LocalDataBuffer() {
	}

	public LocalDataBuffer getInstance() {
		return instance;
	}

	private String appToken = null;
	private String userToken = null;

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

}
