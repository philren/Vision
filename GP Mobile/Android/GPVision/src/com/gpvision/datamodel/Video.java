package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
	private String uuid;
	private String storeName;
	private String originalName;
	private Status status;

	public Video() {
		super();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String name) {
		this.originalName = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		uploading, paused, indexing, indexed, failed, deleted, uploaded
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uuid);
		dest.writeString(storeName);
		dest.writeString(originalName);
		dest.writeString(status.name());
	}

	public Video(Parcel source) {
		uuid = source.readString();
		storeName = source.readString();
		originalName = source.readString();
		status = Status.valueOf(source.readString());
	}

	public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {

		@Override
		public Video createFromParcel(Parcel source) {
			return new Video(source);
		}

		@Override
		public Video[] newArray(int size) {
			return new Video[size];
		}
	};
}
