package com.gpvision.activity;

import java.util.HashMap;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetIndexRequest;
import com.gpvision.api.response.GetIndexResponse;
import com.gpvision.datamodel.Index;
import com.gpvision.datamodel.Video;
import com.gpvision.ui.MediaPlayUI;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;
import com.gpvision.utils.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FullScreenPlayActivity extends BaseActivity {

	private static final int MESSAGE_START = 2158;
	public static final String ARGS_VIDEO_VIDEO_KEY = "video";
	public static final String ARGS_POSITION_KEY = "position";
	public static final String ARGS_INDEX_KEY = "index";

	private int position;
	private MediaPlayUI mediaPlayer;
	private Video video;
	private HashMap<Integer, Index> indexMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI("Fullscreen create");
		setContentView(R.layout.activity_full_screen_play);

		Intent args = getIntent();
		video = args.getParcelableExtra(ARGS_VIDEO_VIDEO_KEY);
		position = args.getIntExtra(ARGS_POSITION_KEY, 0);
		// HashMap<Integer, Index> indexMap = (HashMap<Integer, Index>) args
		// .getSerializableExtra(ARGS_INDEX_KEY);
		mediaPlayer = (MediaPlayUI) findViewById(R.id.full_screen_play_activity_media_play_ui);
		mediaPlayer.setVideo(video, Model.FullScreen, position);
		mediaPlayer.setOnFullScreenModelListener(new FullScreenModelListener() {

			@Override
			public void onFullScreenModel() {
				onBackPressed();
			}
		});
		getIndex();
	}

	@Override
	public void onBackPressed() {
		// mediaPlayer.pause();
		// Intent intent = new Intent();
		// intent.putExtra(ARGS_POSITION_KEY, mediaPlayer.getCurrentPosition());
		// setResult(RESULT_OK, intent);
		setResult(RESULT_OK);
		finish();
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
							handler.sendEmptyMessage(MESSAGE_START);
						}
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {

					}
				});
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_START:
				if (mediaPlayer.isPrepared()) {
					mediaPlayer.start();
				} else {
					sendEmptyMessageDelayed(MESSAGE_START, 1000);
				}
				break;

			default:
				break;
			}
		}

	};
}
