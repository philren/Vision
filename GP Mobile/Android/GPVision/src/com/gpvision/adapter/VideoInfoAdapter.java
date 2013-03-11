package com.gpvision.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gpvision.R;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.ui.VideoButtons;
import com.gpvision.ui.VideoButtons.VideoStatusChangedListener;
import com.gpvision.utils.AppUtils;

import java.util.ArrayList;

public class VideoInfoAdapter extends BaseAdapter {

	private ArrayList<Video> videos;
	private LayoutInflater inflater;

	public VideoInfoAdapter(ArrayList<Video> videos) {
		this.videos = videos;
	}

	public ArrayList<Video> getVideos() {
		return videos;
	}

	public void setVideos(ArrayList<Video> videos) {
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
	public Video getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		Video video = videos.get(position);
		if (view == null) {
			if (inflater == null)
				inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.layout_video_info_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.videoName = (TextView) view
					.findViewById(R.id.video_info_list_video_name);
			holder.videoStatus = (TextView) view
					.findViewById(R.id.video_info_list_video_status);
			holder.videoButtons = (VideoButtons) view
					.findViewById(R.id.video_info_list_videoButtons);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.videoName.setText(video.getOriginalName());
		if (video.getStatus() != null) {
			if (video.getStatus() == Status.uploading) {
				float x = (float) (video.getUploadedLength() * 1.0 / video
						.getContentLength());
				holder.videoStatus.setText(video.getStatus().name()
						+ AppUtils.precentFormat(x));
			} else {
				holder.videoStatus.setText(video.getStatus().name());
			}
		}
		holder.videoButtons.setListener(listener);
		holder.videoButtons.setPosition(position);
		holder.videoButtons.setVideo(video);
		return view;
	}

	private VideoStatusChangedListener listener = new VideoStatusChangedListener() {

		@Override
		public void remove(int position) {
			videos.remove(position);
			notifyDataSetChanged();
		}

		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}

	};

	private static class ViewHolder {
		TextView videoName;
		TextView videoStatus;
		VideoButtons videoButtons;
	}
}
