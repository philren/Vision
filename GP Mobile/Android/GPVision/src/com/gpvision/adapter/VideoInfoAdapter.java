package com.gpvision.adapter;

import android.net.Uri;
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
import com.gpvision.utils.Environment;
import com.gpvision.utils.LocalDataBuffer;
import java.util.ArrayList;

public class VideoInfoAdapter extends BaseAdapter {
	private static final int MESSAGE_UPDATE_PROGRESS = 1810;

	private ArrayList<Video> videos;
	private LayoutInflater inflater;
	private ArrayList<TextView> status;

	public VideoInfoAdapter(ArrayList<Video> videos) {
		this.videos = videos;
		status = new ArrayList<TextView>();
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
	public Object getItem(int position) {
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
			holder.videoStatus.setText(video.getStatus().name());
			if (video.getStatus() == Status.uploading) {
				status.add(position, holder.videoStatus);
			} else {
				status.add(position, null);
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
			notifyDataSetChanged();
			final long length = video.getContentLength();
			Environment environment = LocalDataBuffer.getInstance()
					.getEnvironment();
			Uri.Builder builder = new Uri.Builder();
			builder.encodedPath(String.format("http://%s",
					environment.getHost()));
			if (!AppUtils.isEmpty(environment.getBasePath())) {
				builder.appendPath(environment.getBasePath());
			}
			builder.appendEncodedPath("api");
			builder.appendEncodedPath("upload");

			UploadFileRequest<UploadFileResponse> request = new UploadFileRequest<UploadFileResponse>(
					builder.toString());

			request.addFile(video.getOriginalName(), "video/mp4",
					video.getOriginalPath());
			request.setCallback(new UploadedProgressCallback() {

				@Override
				public void uploadedProgress(long uploadedBytes) {
					Message msg = new Message();
					msg.what = MESSAGE_UPDATE_PROGRESS;
					msg.arg1 = position;
					msg.obj = uploadedBytes * 100f / length;
					handler.sendMessage(msg);

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
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_UPDATE_PROGRESS:
				int position = msg.arg1;
				float precent = (Float) msg.obj;
				TextView progressText = status.get(position);
				if (progressText != null) {
					progressText.setText(String.format("Uploading %.02f",
							precent));
				}
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
