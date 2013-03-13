package com.gpvision.fragment;

import com.gpvision.R;
import com.gpvision.activity.FullScreenPlayActivity;
import com.gpvision.activity.MainActivity;
import com.gpvision.adapter.ImageAdapter;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.DownLoadImageRequest;
import com.gpvision.api.request.DownLoadImageRequest.DownLoadStatusCallBack;
import com.gpvision.api.request.GetIndexRequest;
import com.gpvision.api.response.DownLoadImageResponse;
import com.gpvision.api.response.GetIndexResponse;
import com.gpvision.datamodel.Index;
import com.gpvision.datamodel.Video;
import com.gpvision.ui.MediaPlayUI;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;
import com.gpvision.utils.Environment;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayFragment extends BaseFragment {

	private static final int TASK_SCAN_TIME = 1000 * 10;
	public static final String ARGS_VIDEO_KEY = "video";
	public static final int REQUEST_CODE_FULL_SCREEN = 101;

	private Video video;
	private MediaPlayUI mediaPlayer;
	private int currentPosition = 0;
	private HashMap<Integer, Index> indexMap;
	private int index;
	private TimerTask task;
	private Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args == null)
			args = savedInstanceState;
		video = args.getParcelable(ARGS_VIDEO_KEY);

		getIndex();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragemnt_video_play, container,
				false);
		TextView videoName = (TextView) view
				.findViewById(R.id.video_play_fragment_video_name);
		videoName.setText(video.getOriginalName());

		mediaPlayer = (MediaPlayUI) view
				.findViewById(R.id.video_play_fragment_media_play);
		mediaPlayer.setVideo(getVideoUri(video.getStoreName()), Model.Normal,
				currentPosition);
		mediaPlayer.setOnFullScreenModelListener(new FullScreenModelListener() {

			@Override
			public void onFullScreenModel() {
				Intent intent = new Intent();
				intent.setClass(getActivity(), FullScreenPlayActivity.class);
				Bundle extras = new Bundle();
				extras.putParcelable(FullScreenPlayActivity.ARGS_VIDEO_URI_KEY,
						getVideoUri(video.getStoreName()));
				extras.putSerializable(FullScreenPlayActivity.ARGS_INDEX_KEY,
						indexMap);
				currentPosition = mediaPlayer.getCurrentPosition();
				extras.putInt(FullScreenPlayActivity.ARGS_POSITION_KEY,
						currentPosition);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CODE_FULL_SCREEN);
			}
		});

		Gallery gallery = (Gallery) view
				.findViewById(R.id.video_play_fragment_indexing_images_gallery);
		gallery.setAdapter(new ImageAdapter());
		gallery.setOnItemClickListener(listener);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_FULL_SCREEN
				&& resultCode == FragmentActivity.RESULT_OK) {
			currentPosition = data.getIntExtra(
					FullScreenPlayActivity.ARGS_POSITION_KEY, 0);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		startScanTask();
	}

	@Override
	public void onPause() {
		super.onPause();
		mediaPlayer.pause();
		stopScanTask();
	}

	private Uri getVideoUri(String storeName) {
		Uri.Builder builder = new Uri.Builder();
		Environment environment = LocalDataBuffer.getInstance()
				.getEnvironment();
		builder.encodedPath(String.format("%s://%s", "https",
				environment.getHost()));
		if (environment.getBasePath() != null) {
			builder.appendEncodedPath(environment.getBasePath());
		}
		builder.appendEncodedPath("public");
		builder.appendEncodedPath("getvideo");
		builder.appendEncodedPath(storeName);
		return builder.build();
	}

	private void getIndex() {
		String fileName = video.getStoreName();
		fileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".json";
		new GetIndexRequest(fileName)
				.start(new APIResponseHandler<GetIndexResponse>() {

					@Override
					public void handleResponse(GetIndexResponse response) {
						indexMap = response.getIndexMap();
						if (mediaPlayer != null) {
							mediaPlayer.setIndexMap(indexMap);
							downLoadImages(indexMap);
							mediaPlayer.startPlay(currentPosition);
						}
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {

					}
				});
	}

	private void downLoadImages(HashMap<Integer, Index> indexMap) {
		DownLoadImageRequest<DownLoadImageResponse> request = new DownLoadImageRequest<DownLoadImageResponse>(
				indexMap);
		request.setCallBack(new DownLoadStatusCallBack() {

			@Override
			public void downLoadStatus(int index) {
				LogUtil.logI("downindex:" + index);
				VideoPlayFragment.this.index = index;
			}
		});
		request.start(new APIResponseHandler<DownLoadImageResponse>() {

			@Override
			public void handleError(Long errorCode, String errorMessage) {

			}

			@Override
			public void handleResponse(DownLoadImageResponse response) {

			}
		});
	}

	private void startScanTask() {
		task = new TimerTask() {

			@Override
			public void run() {
				int index = (mediaPlayer.getCurrentPosition() + TASK_SCAN_TIME) / 250;
				if (index > VideoPlayFragment.this.index) {
					mediaPlayer.start();
				} else {
					mediaPlayer.pause();
				}
			}
		};
		timer = new Timer();
		timer.schedule(task, 0, TASK_SCAN_TIME);
	}

	private void stopScanTask() {
		if (task != null) {
			task.cancel();
		}
		if (timer != null) {
			timer.cancel();
		}
	}

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.logI("click:" + position);
			SaveAndShareFragment fragment = new SaveAndShareFragment();
			MessageCenter.getInstance()
					.sendMessage(
							new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT,
									fragment));
		}
	};
}
