package com.gpvision.fragment;

import com.gpvision.R;
import com.gpvision.activity.FullScreenPlayActivity;
import com.gpvision.datamodel.Video;
import com.gpvision.ui.MediaPlayUI;
import com.gpvision.ui.MediaPlayUI.FullScreenModelListener;
import com.gpvision.ui.MediaPlayUI.Model;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VideoPlayFragment extends BaseFragment {

	public static final String ARGS_VIDEO_KEY = "video";
	public static final int REQUEST_CODE_FULL_SCREEN = 101;

	private Video video;
	private MediaPlayUI mediaPlayer;
	private int currentPosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args == null)
			args = savedInstanceState;
		video = args.getParcelable(ARGS_VIDEO_KEY);
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
		// test only
		mediaPlayer.setVideo(
				Uri.parse("http://192.168.1.100:8080/video/test.mp4"),
				Model.Normal, currentPosition);
		mediaPlayer.setOnFullScreenModelListener(new FullScreenModelListener() {

			@Override
			public void onFullScreenModel() {
				Intent intent = new Intent();
				intent.setClass(getActivity(), FullScreenPlayActivity.class);
				Bundle extras = new Bundle();
				extras.putParcelable(FullScreenPlayActivity.ARGS_VIDEO_KEY,
						video);
				currentPosition = mediaPlayer.getCurrentPosition();
				extras.putInt(FullScreenPlayActivity.ARGS_POSITION_KEY,
						currentPosition);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CODE_FULL_SCREEN);
			}
		});
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
}
