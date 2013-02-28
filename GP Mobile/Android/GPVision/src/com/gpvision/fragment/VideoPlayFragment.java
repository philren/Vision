package com.gpvision.fragment;

import com.gpvision.R;
import com.gpvision.datamodel.Video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VideoPlayFragment extends BaseFragment {

	public static final String ARGS_VIDEO_KEY = "video";
	private Video video;

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
		videoName.setText(video.getName());
		return view;
	}

}
