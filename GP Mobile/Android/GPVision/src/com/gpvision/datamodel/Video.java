package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
	private String uuid;
	private String storeName;
	private String originalName;
	private String mineType;
	private long videoSize;
	private long uploadedSize;
	private int videoLength;
	private int width;
	private int height;
	private String originalPath;
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

	public String getMineType() {
		return mineType;
	}

	public void setMineType(String mineType) {
		this.mineType = mineType;
	}

	public long getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(long contentLength) {
		this.videoSize = contentLength;
	}

	public long getUploadedSize() {
		return uploadedSize;
	}

	public void setUploadedSize(long uploadedLength) {
		this.uploadedSize = uploadedLength;
	}

	public int getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(int videoLength) {
		this.videoLength = videoLength;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		uploading, paused, indexing, indexed, failed, deleted, uploaded, wait_index, encoding, analysing, analysed, unknow
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
		dest.writeString(mineType);
		dest.writeLong(videoSize);
		dest.writeLong(uploadedSize);
		dest.writeInt(videoLength);
		dest.writeInt(width);
		dest.writeInt(height);
		dest.writeString(originalPath);
		dest.writeString(status.name());
	}

	public Video(Parcel source) {
		uuid = source.readString();
		storeName = source.readString();
		originalName = source.readString();
		mineType = source.readString();
		videoSize = source.readLong();
		uploadedSize = source.readLong();
		videoLength = source.readInt();
		width = source.readInt();
		height = source.readInt();
		originalPath = source.readString();
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
