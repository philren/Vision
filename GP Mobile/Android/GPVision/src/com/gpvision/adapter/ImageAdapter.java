package com.gpvision.adapter;

import java.util.ArrayList;

import com.gpvision.R;
import com.gpvision.ui.GalleryImage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ImageAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<String> fileNames;

	public ArrayList<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(ArrayList<String> fileNames) {
		this.fileNames = fileNames;
	}

	@Override
	public int getCount() {
		if (fileNames != null) {
			return fileNames.size();
		}
		return 0;
	}

	@Override
	public String getItem(int position) {
		return fileNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		String fileName = fileNames.get(position);
		if (view == null) {
			if (inflater == null) {
				inflater = LayoutInflater.from(parent.getContext());
			}
			view = inflater.inflate(
					R.layout.layout_video_play_image_gallery_item, parent,
					false);
			holder = new ViewHolder();
			holder.image = (GalleryImage) view
					.findViewById(R.id.video_play_image_gallery_image_view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.image.setFileName(fileName);
		return view;
	}

	private class ViewHolder {
		GalleryImage image;
	}
}
