package com.gpvision.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import com.gpvision.R;
import com.gpvision.adapter.VideoInfoAdapter;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetMediaListRequset;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.ui.LoadingDialog;
import com.gpvision.utils.LogUtil;
import com.gpvision.utils.UploadUtil;

public class VideoInfoFragment extends BaseFragment {
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

		adapter = new VideoInfoAdapter(videos);
		videoInfoList.setAdapter(adapter);

		view.findViewById(R.id.video_info_fragment_upload_more_button)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						new UploadUtil().upload();
					}
				});
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
						videos.get(0).setStatus(Status.Indexed);// test only
//						videos.get(1).setStatus(Status.Uploading);
//						videos.get(2).setStatus(Status.Failed);
						adapter.setVideos(videos);
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
						dialog.dismiss();
					}
				});
	}
}
