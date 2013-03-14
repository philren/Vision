package com.gpvision.activity;

import java.util.HashMap;

import com.gpvision.R;
import com.gpvision.datamodel.Index;
import com.gpvision.ui.MediaPlayUI;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;
import com.gpvision.utils.LogUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class FullScreenPlayActivity extends BaseActivity {

	public static final String ARGS_VIDEO_URI_KEY = "video uri";
	public static final String ARGS_POSITION_KEY = "position";
	public static final String ARGS_INDEX_KEY = "index";

	private int position;
	private MediaPlayUI mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.logI("Fullscreen create");
		setContentView(R.layout.activity_full_screen_play);

		Intent args = getIntent();
		Uri uri = args.getParcelableExtra(ARGS_VIDEO_URI_KEY);
		position = args.getIntExtra(ARGS_POSITION_KEY, 0);
//		HashMap<Integer, Index> indexMap = (HashMap<Integer, Index>) args
//				.getSerializableExtra(ARGS_INDEX_KEY);
		mediaPlayer = (MediaPlayUI) findViewById(R.id.full_screen_play_activity_media_play_ui);
		mediaPlayer.setVideo(uri, Model.FullScreen, position);
		mediaPlayer.seekTo(position);
//		mediaPlayer.setIndexMap(indexMap);
		mediaPlayer.setOnFullScreenModelListener(new FullScreenModelListener() {

			@Override
			public void onFullScreenModel() {
				onBackPressed();
			}
		});
	}

	@Override
	public void onBackPressed() {
		mediaPlayer.pause();
		Intent intent = new Intent();
		intent.putExtra(ARGS_POSITION_KEY, mediaPlayer.getCurrentPosition());
		setResult(RESULT_OK, intent);
		finish();
	}

}
