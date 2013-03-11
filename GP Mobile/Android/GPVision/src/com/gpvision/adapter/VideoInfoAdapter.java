package com.gpvision.adapter;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.UploadFileRequest;
import com.gpvision.api.request.UploadFileRequest.UploadedProgressCallback;
import com.gpvision.api.response.UploadFileResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.ui.VideoButtons;
import com.gpvision.ui.VideoButtons.VideoStatusChangedListener;
import com.gpvision.utils.AppUtils;

import java.util.ArrayList;

public class VideoInfoAdapter extends BaseAdapter {
	private static final int MESSAGE_UPDATE_PROGRESS = 1810;
	private static final int MESSAGE_CANCEL_UPDATE_PROGRESS = 1811;

	private ArrayList<Video> videos;
	private LayoutInflater inflater;
	private ArrayList<UploadFileRequest<UploadFileResponse>> tasks;

	public VideoInfoAdapter(ArrayList<Video> videos) {
		this.videos = videos;
		tasks = new ArrayList<UploadFileRequest<UploadFileResponse>>();
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
			holder.videoButtons.setPosition(position);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Video video = videos.get(position);
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
		holder.videoButtons.setVideo(video, listener);
		return view;
	}

	private VideoStatusChangedListener listener = new VideoStatusChangedListener() {

		@Override
		public void remove(int position) {
			videos.remove(position);
			notifyDataSetChanged();
		}

		@Override
		public void upLoading(final int position, Video video) {
			handler.removeMessages(MESSAGE_UPDATE_PROGRESS);
			handler.sendEmptyMessage(MESSAGE_UPDATE_PROGRESS);
			UploadFileRequest<UploadFileResponse> request = new UploadFileRequest<UploadFileResponse>();
			tasks.add(position, request);
			request.addFile(video.getOriginalName(), "video/mp4",
					video.getOriginalPath());
			request.setCallback(new UploadedProgressCallback() {

				@Override
				public void uploadedProgress(long uploadedBytes) {
					Video video = getItem(position);
					video.setUploadedLength(uploadedBytes);
				}
			});
			request.start(new APIResponseHandler<UploadFileResponse>() {

				@Override
				public void handleResponse(UploadFileResponse response) {

				}

				@Override
				public void handleError(Long errorCode, String errorMessage) {

				}
			});
		}

		@Override
		public void abort(int position, Video video) {
			notifyDataSetChanged();
		}

		@Override
		public void pause(int position, Video video) {
			notifyDataSetChanged();
			tasks.get(position).cancel(true);
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_UPDATE_PROGRESS:
				notifyDataSetChanged();
				handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_PROGRESS, 1000);
				break;
			case MESSAGE_CANCEL_UPDATE_PROGRESS:
				removeMessages(MESSAGE_UPDATE_PROGRESS);
				removeMessages(MESSAGE_CANCEL_UPDATE_PROGRESS);
				break;
			default:
				break;
			}
		}

	};

	private static class ViewHolder {
		TextView videoName;
		TextView videoStatus;
		VideoButtons videoButtons;
	}
}
