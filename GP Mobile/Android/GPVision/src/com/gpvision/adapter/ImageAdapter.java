package com.gpvision.adapter;

import com.gpvision.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			if (inflater == null) {
				inflater = LayoutInflater.from(parent.getContext());
			}
			view = inflater.inflate(
					R.layout.layout_video_play_image_gallery_item, parent,
					false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view
					.findViewById(R.id.video_play_image_gallery_image_view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.imageView.setImageResource(R.drawable.ic_launcher);
		return view;
	}

	private class ViewHolder {
		ImageView imageView;
	}
}
