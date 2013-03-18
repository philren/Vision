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
import com.gpvision.ui.MediaController.Callback;
import com.gpvision.ui.MediaPlayUI;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;
import com.gpvision.ui.dialog.LoadingDialog;
import com.gpvision.utils.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayFragment extends BaseFragment {

	public static final String TAG = VideoPlayFragment.class.getName();
	private static final int MESSAGE_UPDATE_GALLERY = 1736;
	private static final int TASK_SCAN_TIME = 1000;
	public static final String ARGS_VIDEO_KEY = "video";
	public static final int REQUEST_CODE_FULL_SCREEN = 101;

	private Video video;
	private MediaPlayUI mediaPlayer;
	private ImageAdapter adapter;
	private int currentPosition = 0;
	private HashMap<Integer, Index> indexMap;
	private int index;
	private static boolean isManual = false;
	private LoadingDialog dialog;
	private Gallery gallery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new LoadingDialog(getActivity());
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
		mediaPlayer.setCallback(callback);
		mediaPlayer.setVideo(video, Model.Normal, currentPosition);
		mediaPlayer.setIndexMap(indexMap);
		mediaPlayer.setOnFullScreenModelListener(new FullScreenModelListener() {

			@Override
			public void onFullScreenModel() {
				Intent intent = new Intent();
				intent.setClass(getActivity(), FullScreenPlayActivity.class);
				Bundle extras = new Bundle();
				extras.putParcelable(
						FullScreenPlayActivity.ARGS_VIDEO_VIDEO_KEY, video);
				// extras.putSerializable(FullScreenPlayActivity.ARGS_INDEX_KEY,
				// indexMap);
				currentPosition = mediaPlayer.getCurrentPosition();
				extras.putInt(FullScreenPlayActivity.ARGS_POSITION_KEY,
						currentPosition);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CODE_FULL_SCREEN);
			}
		});
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		gallery = (Gallery) view
				.findViewById(R.id.video_play_fragment_indexing_images_gallery);

		MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
		mlp.setMargins(-(metrics.widthPixels / 2), mlp.topMargin,
				mlp.rightMargin, mlp.bottomMargin);
		adapter = new ImageAdapter();
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(listener);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_FULL_SCREEN) {
			// currentPosition = data.getIntExtra(
			// FullScreenPlayActivity.ARGS_POSITION_KEY, 0);
			mediaPlayer.setVideo(video, Model.Normal, currentPosition);
			mediaPlayer.setIndexMap(indexMap);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// mediaPlayer.setVideo(video, Model.Normal, currentPosition);
		// mediaPlayer.setIndexMap(indexMap);
		handler.sendEmptyMessage(MESSAGE_UPDATE_GALLERY);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeMessages(MESSAGE_UPDATE_GALLERY);
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
						}
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
					}
				});
	}

	private void downLoadImages(HashMap<Integer, Index> indexMap) {
		DownLoadImageRequest<DownLoadImageResponse> request = new DownLoadImageRequest<DownLoadImageResponse>(
				indexMap);
		request.setCallBack(new DownLoadStatusCallBack() {

			@Override
			public void downLoadStatus(int index) {
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

	private ArrayList<String> getImageNames(int position) {
		if (indexMap == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		int index = position / 250;
		int from = index - TASK_SCAN_TIME / 250;
		int to = index + TASK_SCAN_TIME / 250;
		for (int i = from; i < to; i++) {
			if (indexMap.containsKey(i)) {
				ArrayList<String> imageUrls = indexMap.get(i).getImageUrls();
				for (String url : imageUrls) {
					list.add(ImageCacheUtil.getFileNameFromUrl(url));
				}
			}
		}
		return list;
	}

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (isManual) {
				String fileName = adapter.getItem(position);
				SaveAndShareFragment fragment = new SaveAndShareFragment();
				Bundle args = new Bundle();
				args.putString(SaveAndShareFragment.ARGS_FILE_NAME_KEK,
						fileName);
				fragment.setArguments(args);
				MessageCenter.getInstance().sendMessage(
						new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT,
								fragment));
			}
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_UPDATE_GALLERY:
				if (!isManual) {
					int position = mediaPlayer.getCurrentPosition();
					if (index > position / 250) {
						if (mediaPlayer.isPrepared()) {
							mediaPlayer.start();
							adapter.setFileNames(getImageNames(position));
							adapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					} else {
						if (mediaPlayer.isPrepared()) {
							mediaPlayer.pause();
						}
						dialog.show();
					}
				}
				sendEmptyMessageDelayed(MESSAGE_UPDATE_GALLERY, TASK_SCAN_TIME);
				break;

			default:
				break;
			}
		}

	};

	private Callback callback = new Callback() {

		@Override
		public void onManualModel(boolean isManual) {
			VideoPlayFragment.isManual = isManual;
		}
	};
}
