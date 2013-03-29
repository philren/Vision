package com.gpvision.fragment;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.adapter.VideoInfoAdapter;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetMediaListRequset;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.fragment.ChooseFileFragment.OnChoseListener;
import com.gpvision.ui.VideoButtons.VideoStatusChangedListener;
import com.gpvision.ui.dialog.LoadingDialog;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LogUtil;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;
import com.gpvision.utils.UploadManage;
import com.gpvision.utils.UploadManage.UploadStatusCallback;

public class VideoInfoFragment extends BaseFragment {
	public static final String TAG = VideoInfoFragment.class.getName();
	private static final int MSG_DATA_CHANGED = 1735;
	private VideoInfoAdapter adapter;
	private ArrayList<Video> videos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getVideoList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video_info, container,
				false);
		ListView videoInfoList = (ListView) view
				.findViewById(R.id.video_info_fragment_list);
		if (adapter == null)
			adapter = new VideoInfoAdapter(videos, listener);
		videoInfoList.setAdapter(adapter);

		Button uploadButton = (Button) view
				.findViewById(R.id.video_info_fragment_upload_more_button);
		uploadButton.setOnClickListener(this);
		return view;
	}

	private void getVideoList() {
		final LoadingDialog dialog = new LoadingDialog(getActivity());
		dialog.show();
		new GetMediaListRequset()
				.start(new APIResponseHandler<GetMediaListResponse>() {

					@Override
					public void handleResponse(GetMediaListResponse response) {
						videos = response.getVideos();
						// clean status deleted
						for (Video video : videos) {
							if (video.getStatus() == Status.deleted) {
								videos.remove(video);
							}
						}
						if (adapter != null) {
							adapter.setVideos(videos);
							adapter.notifyDataSetChanged();
						}
						dialog.dismiss();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
						dialog.dismiss();
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_info_fragment_upload_more_button:
			FragmentManager manager = getFragmentManager();
			ChooseFileFragment fragment = (ChooseFileFragment) manager
					.findFragmentByTag(ChooseFileFragment.TAG);
			if (fragment == null)
				fragment = new ChooseFileFragment();
			fragment.setOnChoseListener(new OnChoseListener() {

				@Override
				public void onChose(File file) {
					Video video = new Video();
					video.setOriginalName(file.getName());
					video.setStatus(Status.uploading);
					video.setOriginalPath(file.getAbsolutePath());
					video.setMd5(AppUtils.getMd5(file.getAbsolutePath()));
					video.setVideoSize(file.length());
					if (videos == null) {
						videos = new ArrayList<Video>();
					}
					videos.add(0, video);
					adapter.notifyDataSetChanged();
					UploadManage manage = UploadManage.getInstance();
					manage.addTask(video);
					manage.setCallback(callback);
				}
			});
			MessageCenter.getInstance().sendMessage(
					new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT, fragment,
							ChooseFileFragment.TAG));
			break;

		default:
			break;
		}
	}

	private VideoStatusChangedListener listener = new VideoStatusChangedListener() {

		@Override
		public void delete() {
			getVideoList();
		}

		@Override
		public void onChanged(int position, Video video) {
			if (adapter != null) {
				if (position >= 0)
					adapter.getVideos().set(position, video);
				mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
			}
		}

		@Override
		public void onPlay(Video video) {
			FragmentManager manager = getFragmentManager();
			VideoPlayFragment fragment = (VideoPlayFragment) manager
					.findFragmentByTag(VideoPlayFragment.TAG);
			if (fragment == null)
				fragment = new VideoPlayFragment();
			fragment.setVideo(video);
			MessageCenter.getInstance().sendMessage(
					new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT, fragment,
							VideoPlayFragment.TAG));
		}

		@Override
		public void onUploading(int position, Video video) {
			UploadManage.getInstance().addTask(video);
			adapter.getVideos().set(position, video);
			mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
		}

		@Override
		public void onPaused(int position, Video video) {
			UploadManage.getInstance().cancelTask(video.getMd5());
			adapter.getVideos().set(position, video);
			mHandler.sendEmptyMessage(MSG_DATA_CHANGED);

		}

	};

	private UploadStatusCallback callback = new UploadStatusCallback() {

		@Override
		public void finished(Video video) {

		}

		@Override
		public void changed(Video video) {
			int size = videos.size();
			for (int i = 0; i < size; i++) {
				Video v = videos.get(i);
				if (v.getMd5() != null && v.getMd5().equals(video.getMd5())) {
					videos.set(i, video);
					mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
					break;
				}
			}
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_DATA_CHANGED:
				if (adapter != null)
					adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	};
}
