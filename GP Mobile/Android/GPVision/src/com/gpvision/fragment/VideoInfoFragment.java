package com.gpvision.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gpvision.R;
import com.gpvision.adapter.VideoInfoAdapter;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;

public class VideoInfoFragment extends BaseFragment {
	private VideoInfoAdapter adapter;
	private ArrayList<Video> videos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO call API
		videos = new ArrayList<Video>();// test only
		Video video1 = new Video();
		video1.setOriginalName("first.mp4");
		video1.setStatus(Status.Uploading);
		Video video2 = new Video();
		video2.setOriginalName("second.avi");
		video2.setStatus(Status.Indexed);
		Video video3 = new Video();
		video3.setOriginalName("third.ogg");
		video3.setStatus(Status.Deleted);
		videos.add(video1);
		videos.add(video2);
		videos.add(video3);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video_info, container,
				false);
		ListView videoInfoList = (ListView) view
				.findViewById(R.id.video_info_fragment_list);

		adapter = new VideoInfoAdapter(videos);
		videoInfoList.setAdapter(adapter);
		return view;
	}
}
