package com.gpvision.fragment;

import java.io.File;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.adapter.VideoInfoAdapter;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetMediaListRequset;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.db.DBUtil;
import com.gpvision.fragment.ChooseFileFragment.OnChoseListener;
import com.gpvision.service.DataManage;
import com.gpvision.service.DataMessage;
import com.gpvision.ui.VideoButtons.VideoStatusChangedListener;
import com.gpvision.ui.dialog.ErrorDialog;
import com.gpvision.ui.dialog.LoadingDialog;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LogUtil;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;
import com.gpvision.utils.UploadManage.UploadStatusCallback;

public class VideoInfoFragment extends BaseFragment {
	public static final String TAG = VideoInfoFragment.class.getName();
	private static final long MAX_SPACE_USER = 1024 * 1024 * 1024 * 2L;// 2GB
	private static final int MSG_DATA_CHANGED = 1735;
	private VideoInfoAdapter mAdapter;
	private ArrayList<Video> mVideos;
	private DataManage dataManage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataManage = new DataManage();
		dataManage.bindService(getActivity());
		dataManage.setCallback(callback);
		getVideoList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dataManage.unBindService(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video_info, container,
				false);
		ListView videoInfoList = (ListView) view
				.findViewById(R.id.video_info_fragment_list);
		if (mAdapter == null)
			mAdapter = new VideoInfoAdapter(mVideos, listener);
		videoInfoList.setAdapter(mAdapter);

		Button uploadButton = (Button) view
				.findViewById(R.id.video_info_fragment_upload_more_button);
		uploadButton.setOnClickListener(this);
		return view;
	}

	private void getVideoList() {
		final LoadingDialog dialog = new LoadingDialog(getActivity());
		dialog.show();
		new GetMediaListRequset()
				.start(new APIResponseHandler<GetMediaListResponse>() {

					@Override
					public void handleResponse(GetMediaListResponse response) {
						ArrayList<Video> videos = response.getVideos();
						// clean status deleted
						for (Video video : videos) {
							if (video.getStatus() == Status.deleted) {
								videos.remove(video);
							}
						}
						if (mAdapter != null) {
							DBUtil db = new DBUtil(getActivity());
							ArrayList<Video> list = db.query();
							if (list != null)
								videos.addAll(0, list);
							mAdapter.setVideos(videos);
							mAdapter.notifyDataSetChanged();
							db.close();
						}
						dialog.dismiss();
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
						dialog.dismiss();
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_info_fragment_upload_more_button:
			FragmentManager manager = getFragmentManager();
			ChooseFileFragment fragment = (ChooseFileFragment) manager
					.findFragmentByTag(ChooseFileFragment.TAG);
			if (fragment == null)
				fragment = new ChooseFileFragment();
			fragment.setOnChoseListener(new OnChoseListener() {

				@Override
				public void onChose(final File file) {
					new AsyncTask<Void, Void, Video>() {
						LoadingDialog dialog;

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							dialog = new LoadingDialog(getActivity());
							dialog.show();
						}

						@Override
						protected Video doInBackground(Void... params) {
							Video video = new Video();
							String fileName = file.getName();
							video.setOriginalName(fileName);
							video.setStatus(Video.Status.uploading);
							video.setOriginalPath(file.getAbsolutePath());
							video.setMd5(AppUtils.getMd5(file.getAbsolutePath()));
							video.setVideoSize(file.length());
							video.setMineType("video/"
									+ fileName.substring(fileName
											.lastIndexOf(".") + 1));
							return video;
						}

						@Override
						protected void onPostExecute(Video video) {
							super.onPostExecute(video);
							ArrayList<Video> videos = mAdapter.getVideos();
							if (videos == null) {
								videos = new ArrayList<Video>();
							}
							String name = video.getOriginalName();
							String type = video.getMineType();
							if (!(type.equals("video/mp4")
									|| type.equals("video/ogg") || type
									.equals("video/webm"))) {
								new ErrorDialog(
										getActivity(),
										R.string.base_error_title,
										R.string.video_in_fragment_error_message_mine_type_error);
								dialog.dismiss();
								return;
							}
							long sizeCount = video.getVideoSize();
							for (Video v : videos) {
								sizeCount += v.getVideoSize();
								if (name.equals(v.getOriginalName())) {
									new ErrorDialog(
											getActivity(),
											R.string.base_error_title,
											R.string.video_in_fragment_error_message_file_exist_error);
									dialog.dismiss();
									return;
								}
							}
							if (sizeCount > MAX_SPACE_USER) {
								LogUtil.logI("size:" + sizeCount);
								new ErrorDialog(
										getActivity(),
										R.string.base_error_title,
										R.string.video_in_fragment_error_message_user_space_error);
								dialog.dismiss();
								return;
							}
							videos.add(0, video);
							mAdapter.notifyDataSetChanged();
							dataManage.sendMessage(new DataMessage(
									DataManage.MSG_ADD_TASK, video));
							dialog.dismiss();
						}
					}.execute();

				}
			});
			MessageCenter.getInstance().sendMessage(
					new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT, fragment,
							ChooseFileFragment.TAG));
			break;

		default:
			break;
		}
	}

	private VideoStatusChangedListener listener = new VideoStatusChangedListener() {

		@Override
		public void delete(int position, Video video) {
			if (AppUtils.isEmpty(video.getUuid())) {
				dataManage.sendMessage(new DataMessage(
						DataManage.MSG_DELETE_TASK, video));
				mAdapter.getVideos().remove(position);
				mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
			} else {
				getVideoList();
			}
		}

		@Override
		public void onChanged(int position, Video video) {
			if (mAdapter != null) {
				if (position >= 0)
					mAdapter.getVideos().set(position, video);
				mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
			}
		}

		@Override
		public void onPlay(Video video) {
			FragmentManager manager = getFragmentManager();
			VideoPlayFragment fragment = (VideoPlayFragment) manager
					.findFragmentByTag(VideoPlayFragment.TAG);
			if (fragment == null)
				fragment = new VideoPlayFragment();
			fragment.setVideo(video);
			MessageCenter.getInstance().sendMessage(
					new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT, fragment,
							VideoPlayFragment.TAG));
		}

		@Override
		public void onUploading(int position, Video video) {
			mAdapter.getVideos().set(position, video);
			mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
			dataManage.sendMessage(new DataMessage(DataManage.MSG_ADD_TASK,
					video));
		}

		@Override
		public void onPaused(int position, Video video) {
			dataManage.sendMessage(new DataMessage(DataManage.MSG_CANCEL_TASK,
					video));
			mAdapter.getVideos().set(position, video);
			mHandler.sendEmptyMessage(MSG_DATA_CHANGED);

		}

		@Override
		public void onAbort(int position, Video video) {
			dataManage.sendMessage(new DataMessage(DataManage.MSG_ABORT_TASK,
					video));
			mAdapter.getVideos().set(position, video);
			mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
		}

	};

	private UploadStatusCallback callback = new UploadStatusCallback() {

		@Override
		public void finished(Video video) {
			LogUtil.logI(video.getOriginalName() + "---finished");
			ArrayList<Video> videos = mAdapter.getVideos();
			int size = videos.size();
			for (int i = 0; i < size; i++) {
				Video v = videos.get(i);
				if (v.getMd5() != null && v.getMd5().equals(video.getMd5())) {
					videos.set(i, video);
					mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
					break;
				}
			}
		}

		@Override
		public void changed(Video video) {
			ArrayList<Video> videos = mAdapter.getVideos();
			int size = videos.size();
			for (int i = 0; i < size; i++) {
				Video v = videos.get(i);
				if (v.getMd5() != null && v.getMd5().equals(video.getMd5())) {
					videos.set(i, video);
					mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
					break;
				}
			}
		}

		@Override
		public void onError(int errorCode, Video video) {
			ArrayList<Video> videos = mAdapter.getVideos();
			int size = videos.size();
			for (int i = 0; i < size; i++) {
				Video v = videos.get(i);
				if (v.getMd5() != null && v.getMd5().equals(video.getMd5())) {
					videos.set(i, video);
					mHandler.sendEmptyMessage(MSG_DATA_CHANGED);
					break;
				}
			}
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_DATA_CHANGED:
				if (mAdapter != null)
					mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	};

}
