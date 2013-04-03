package com.gpvision.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetUploadedSizeRequest;
import com.gpvision.api.request.UploadFileRequest;
import com.gpvision.api.request.UploadFileRequest.UploadedProgressCallback;
import com.gpvision.api.response.GetUploadedSizeResponse;
import com.gpvision.api.response.UploadFileResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;

public class UploadManage {

	private static UploadManage instance;
	private HashMap<String, Pair> taskMap;
	private UploadStatusCallback mCallback;

	private UploadManage() {
		super();
		taskMap = new HashMap<String, Pair>();
	}

	public void setCallback(UploadStatusCallback callback) {
		this.mCallback = callback;
	}

	public static UploadManage getInstance() {
		if (instance == null) {
			synchronized (UploadManage.class) {
				if (instance == null)
					instance = new UploadManage();
			}
		}
		return instance;
	}

	public void addTask(Video video) {
		if (!taskMap.containsKey(video.getMd5())) {
			taskMap.put(video.getMd5(), new Pair(video, null));
		} else {
			taskMap.get(video.getMd5()).video = video;
		}
		startTask(video.getMd5());
	}

	private void startTask(final String key) {
		Pair pair = taskMap.get(key);
		if (pair.video.getStatus() == Status.uploading) {
			// if (pair.uploadFileRequest != null
			// && ((!pair.uploadFileRequest.isCancelled())||
			// pair.uploadFileRequest.getStatus()!= AsyncTask.Status.FINISHED))
			// {
			// // TODO task has set or running
			//
			// } else {
			new GetUploadedSizeRequest(pair.video.getOriginalName(),
					pair.video.getMd5(), pair.video.getVideoSize(), Calendar
							.getInstance().getTimeInMillis())
					.start(new APIResponseHandler<GetUploadedSizeResponse>() {

						@Override
						public void handleResponse(
								GetUploadedSizeResponse response) {
							long uploadedSize = response.getUploadedSize();
							setUploadRequest(key, uploadedSize);
						}

						@Override
						public void handleError(Long errorCode,
								String errorMessage) {
							LogUtil.logE("error:" + errorCode);
						}
					});
			// }
		}
	}

	public void cancelTask(String key) {
		Pair pair = taskMap.get(key);
		if (pair.uploadFileRequest != null)
			pair.uploadFileRequest.cancel(true);
	}

	public void cancelAllTask() {
		Set<String> kSet = taskMap.keySet();
		for (Iterator<String> iterator = kSet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Pair pair = taskMap.get(key);
			if (pair.uploadFileRequest != null)
				pair.uploadFileRequest.cancel(true);
		}
	}

	public ArrayList<Video> getUploadStatus() {
		Set<String> kSet = taskMap.keySet();
		ArrayList<Video> videos = new ArrayList<Video>();
		for (Iterator<String> iterator = kSet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Pair pair = taskMap.get(key);
			videos.add(pair.video);
		}
		return videos;
	}

	private void setUploadRequest(final String key, long uploadedSize) {
		Pair pair = taskMap.get(key);
		pair.uploadFileRequest = new UploadFileRequest<UploadFileResponse>();
		pair.uploadFileRequest.addFile(pair.video.getOriginalName(),
				pair.video.getMineType(), pair.video.getOriginalPath(),
				uploadedSize);
		pair.uploadFileRequest.setCallback(new UploadedProgressCallback() {

			@Override
			public void uploadedProgress(long uploadedBytes) {
				Pair pair = taskMap.get(key);
				pair.video.setUploadedSize(uploadedBytes);
				if (mCallback != null)
					mCallback.changed(pair.video);
			}
		});
		pair.uploadFileRequest
				.start(new APIResponseHandler<UploadFileResponse>() {

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE("error:" + errorCode);
						if (mCallback != null) {
							Video video = taskMap.get(key).video;
							video.setStatus(Status.paused);
							mCallback.onError(errorCode.intValue(), video);
						}
					}

					@Override
					public void handleResponse(UploadFileResponse response) {
						if (mCallback != null) {
							Video video = taskMap.get(key).video;
							video.setStatus(Status.uploaded);
							mCallback.finished(video);
						}
					}
				});

	}

	private class Pair {
		Video video;
		UploadFileRequest<UploadFileResponse> uploadFileRequest;

		public Pair(Video video,
				UploadFileRequest<UploadFileResponse> uploadFileRequest) {
			super();
			this.video = video;
			this.uploadFileRequest = uploadFileRequest;
		}

	}

	public interface UploadStatusCallback {
		public void changed(Video video);

		public void finished(Video video);

		public void onError(int errorCode, Video video);
	}
}
