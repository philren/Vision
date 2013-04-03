package com.gpvision.service;

import android.os.Parcel;
import android.os.Parcelable;

public class DataMessage implements Parcelable {

	public int what;
	public Object data;

	/**
	 * only used for ipc
	 * 
	 * @param what
	 * @param data
	 */
	public DataMessage(int what, Object data) {
		super();
		this.what = what;
		this.data = data;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(what);
		dest.writeValue(data);
	}

	public DataMessage(Parcel source) {
		what = source.readInt();
		data = source.readValue(DataMessage.class.getClassLoader());
	}

	public static Parcelable.Creator<DataMessage> CREATOR = new Parcelable.Creator<DataMessage>() {

		@Override
		public DataMessage createFromParcel(Parcel source) {
			return new DataMessage(source);
		}

		@Override
		public DataMessage[] newArray(int size) {
			return new DataMessage[size];
		}

	};
}
