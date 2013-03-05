package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {

	private long id;
	private String account;
	private String password;
	private String appToken;
	private String userToken;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(account);
		dest.writeString(password);
		dest.writeString(appToken);
		dest.writeString(userToken);
	}

	public Account(Parcel source) {
		id = source.readLong();
		account = source.readString();
		password = source.readString();
		appToken = source.readString();
		userToken = source.readString();
	}

	public Account() {
	}

	public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {

		@Override
		public Account createFromParcel(Parcel source) {
			return new Account(source);
		}

		@Override
		public Account[] newArray(int size) {
			return new Account[size];
		}
	};
}
