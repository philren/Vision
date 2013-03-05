package com.gpvision.ui;

import com.gpvision.datamodel.Account;

public class LocalDataBuffer {
	private static LocalDataBuffer instance = new LocalDataBuffer();

	private LocalDataBuffer() {
	}

	public static LocalDataBuffer getInstance() {
		return instance;
	}

	private Account account;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
