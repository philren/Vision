package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
	private long id;
	private String name;
	private Status status;

	public Video() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		Uploading, Paused, Indexing, Indexed, Failed, Deleted
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(status.name());
	}

	public Video(Parcel source) {
		id = source.readLong();
		name = source.readString();
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
