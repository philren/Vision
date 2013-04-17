package com.gpvision.fragment;

import com.gpvision.R;
import com.gpvision.activity.FullScreenPlayActivity;
import com.gpvision.activity.MainActivity;
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
import com.gpvision.ui.MyGallery;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;
import com.gpvision.ui.MyGallery.OnItemClickListener;
import com.gpvision.ui.dialog.LoadingDialog;
import com.gpvision.utils.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoPlayFragment extends BaseFragment {

	public static final String TAG = VideoPlayFragment.class.getName();
	private static final int MESSAGE_UPDATE_GALLERY = 1736;
	private static final int TASK_SCAN_TIME = 1000;
	public static final int REQUEST_CODE_FULL_SCREEN = 101;

	private Video video;
	private MediaPlayUI mediaPlayer;
	private int currentPosition = 0;
	private HashMap<Integer, ArrayList<Index>> indexMap;
	private int indexKey;
	private static boolean isManual = false;
	private LoadingDialog dialog;
	private MyGallery gallery;

	public void setVideo(Video video) {
		this.video = video;
		getIndexKey();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (dialog == null)
			dialog = new LoadingDialog(getActivity());
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

		gallery = (MyGallery) view
				.findViewById(R.id.video_play_fragment_indexing_images_gallery);
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

	private void getIndexKey() {
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
						ApiErrorHandler.handler(getActivity(), errorCode);
					}
				});
	}

	private void downLoadImages(HashMap<Integer, ArrayList<Index>> indexMap) {
		DownLoadImageRequest<DownLoadImageResponse> request = new DownLoadImageRequest<DownLoadImageResponse>(
				indexMap);
		request.setCallBack(new DownLoadStatusCallBack() {

			@Override
			public void downLoadStatus(int index) {
				VideoPlayFragment.this.indexKey = index;
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

	private ArrayList<String> getImageDirs(int position) {
		if (indexMap == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		int p = position / 250;
		int from = p - TASK_SCAN_TIME / 250 / 2;
		int to = p + TASK_SCAN_TIME / 250 / 2;
		for (int i = from; i < to; i++) {
			if (indexMap.containsKey(i)) {
				ArrayList<Index> indexs = indexMap.get(i);
				for (Index index : indexs) {
					String url = index.getImageUrl();
					String t = ImageCacheUtil.getChildDir(url);
					if (!list.contains(t))
						list.add(t);
				}
			}
		}
		return list;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_UPDATE_GALLERY:
				if (!isManual) {
					int position = mediaPlayer.getCurrentPosition();
					if (indexKey > position / 250) {
						if (mediaPlayer.isPrepared()) {
							mediaPlayer.start();
							dialog.dismiss();
						}
					} else {
						if (mediaPlayer.isPrepared()) {
							mediaPlayer.pause();
						}
						dialog.show();
					}
					gallery.setImageChildDirs(getImageDirs(position));
				}
				sendEmptyMessageDelayed(MESSAGE_UPDATE_GALLERY, TASK_SCAN_TIME);
				break;

			default:
				break;
			}
		}

	};

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClickListener(String childDir) {
			if (isManual) {
				FragmentManager manager = getFragmentManager();
				SaveAndShareFragment fragment = (SaveAndShareFragment) manager
						.findFragmentByTag(SaveAndShareFragment.TAG);
				if (fragment == null)
					fragment = new SaveAndShareFragment();
				fragment.setChildDir(childDir);
				MessageCenter.getInstance().sendMessage(
						new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT,
								fragment, SaveAndShareFragment.TAG));
			}
		}

	};

	private Callback callback = new Callback() {

		@Override
		public void onManualModel(boolean isManual) {
			VideoPlayFragment.isManual = isManual;
			gallery.setTouchable(isManual);
		}
	};
}
