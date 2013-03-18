package com.gpvision.fragment;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
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
import com.gpvision.utils.LogUtil;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;

public class VideoInfoFragment extends BaseFragment {
	public static final String TAG = VideoInfoFragment.class.getName();
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
			ChooseFileFragment fragment = new ChooseFileFragment();
			fragment.setOnChoseListener(new OnChoseListener() {

				@Override
				public void onChose(File file) {
					Video video = new Video();
					video.setOriginalName(file.getName());
					video.setStatus(Status.uploading);
					video.setOriginalPath(file.getAbsolutePath());
					video.setContentLength(file.length());
					if (videos == null) {
						videos = new ArrayList<Video>();
					}
					videos.add(0, video);
					adapter.notifyDataSetChanged();
				}
			});
			MessageCenter.getInstance()
					.sendMessage(
							new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT,
									fragment));
			break;

		default:
			break;
		}
	}

	private VideoStatusChangedListener listener = new VideoStatusChangedListener() {

		@Override
		public void delete(int position) {
			getVideoList();
		}

		@Override
		public void onChanged() {
			if (adapter != null)
				adapter.notifyDataSetChanged();
		}

	};
}
