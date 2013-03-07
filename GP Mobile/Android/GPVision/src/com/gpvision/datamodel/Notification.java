package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {

	private String title;
	private String message;

	public Notification() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(message);
	}

	public Notification(Parcel source) {
		title = source.readString();
		message = source.readString();
	}

	public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {

		@Override
		public Notification createFromParcel(Parcel source) {
			return new Notification(source);
		}

		@Override
		public Notification[] newArray(int size) {
			return new Notification[size];
		}
	};
}
