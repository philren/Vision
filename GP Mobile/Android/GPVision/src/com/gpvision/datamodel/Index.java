package com.gpvision.datamodel;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class Index implements Parcelable {

	private ArrayList<Location> locations;
	private ArrayList<String> imageUrls;

	public Index() {
		super();
	}

	public ArrayList<Location> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<Location> locations) {
		this.locations = locations;
	}

	public ArrayList<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(ArrayList<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(locations);
		dest.writeList(imageUrls);
	}

	public Index(Parcel source) {
		locations = new ArrayList<Location>();
		source.readList(locations, Location.class.getClassLoader());
		imageUrls = new ArrayList<String>();
		source.readList(imageUrls, String.class.getClassLoader());
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
