package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {

	private int left;
	private int top;
	private int height;
	private int width;

	public Location() {
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(left);
		dest.writeInt(top);
		dest.writeInt(height);
		dest.writeInt(width);
	}

	public Location(Parcel source) {
		left = source.readInt();
		top = source.readInt();
		height = source.readInt();
		width = source.readInt();
	}

	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {

		@Override
		public Location createFromParcel(Parcel source) {
			return new Location(source);
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};
}
