package com.gpvision.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Index implements Parcelable {

	private Location location;
	private String imageUrl;

	public Index() {
		super();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(location, flags);
		dest.writeString(imageUrl);
	}

	public Index(Parcel source) {
		location = source.readParcelable(Location.class.getClassLoader());
		imageUrl = source.readString();
	}

	public static final Parcelable.Creator<Index> COEATOR = new Parcelable.Creator<Index>() {

		@Override
		public Index createFromParcel(Parcel source) {
			return new Index(source);
		}

		@Override
		public Index[] newArray(int size) {
			return new Index[size];
		}
	};
}
