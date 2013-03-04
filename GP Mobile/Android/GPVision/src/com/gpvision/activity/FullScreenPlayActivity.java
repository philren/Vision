package com.gpvision.activity;

import com.gpvision.R;
import com.gpvision.datamodel.Video;
import com.gpvision.ui.MediaPlayUI;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class FullScreenPlayActivity extends BaseActivity {

	public static final String ARGS_VIDEO_KEY = "video";
	public static final String ARGS_POSITION_KEY = "position";

	private Video video;
	private int position;
	private MediaPlayUI mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_play);

		Intent args = getIntent();
		video = args.getParcelableExtra(ARGS_VIDEO_KEY);
		position = args.getIntExtra(ARGS_POSITION_KEY, 0);
		mediaPlayer = (MediaPlayUI) findViewById(R.id.full_screen_play_activity_media_play_ui);
		mediaPlayer.setVideo(
				Uri.parse("http://192.168.1.100:8080/video/test.mp4"),
				Model.FullScreen, position);
		mediaPlayer.seekTo(position);
		mediaPlayer.setOnFullScreenModelListener(new FullScreenModelListener() {

			@Override
			public void onFullScreenModel() {
				onBackPressed();
			}
		});
	}

	@Override
	public void onBackPressed() {
		mediaPlayer.stopPlayer();
		super.onBackPressed();
	}

}
