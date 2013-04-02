package com.gpvision.utils;

import com.gpvision.datamodel.Account;

public class LocalDataBuffer {
	private static LocalDataBuffer instance = new LocalDataBuffer();

	private LocalDataBuffer() {
		environment = Environment.E9S;
		videoEnvironment = Environment.E9;
	}

	public static LocalDataBuffer getInstance() {
		return instance;
	}

	private Account account;
	private Environment environment;
	private Environment videoEnvironment;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public Environment getVideoEnvironment() {
		return videoEnvironment;
	}

}
