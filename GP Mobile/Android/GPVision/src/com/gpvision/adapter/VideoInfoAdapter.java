package com.gpvision.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.gpvision.datamodel.Video;

import java.util.ArrayList;

public class VideoInfoAdapter extends BaseAdapter {
	private ArrayList<Video> videos;

	public VideoInfoAdapter(ArrayList<Video> videos) {
		this.videos = videos;
	}

	@Override
	public int getCount() {
		if (videos != null) {
			return videos.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		return null;
	}
}
