package com.gpvision.utils;

import java.util.Calendar;
import java.util.HashMap;

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
			if (pair.uploadFileRequest != null
					&& !pair.uploadFileRequest.isCancelled()) {
				// TODO task has set or running

			} else {
				new GetUploadedSizeRequest(pair.video.getOriginalName(),
						pair.video.getMd5(), pair.video.getVideoSize(),
						Calendar.getInstance().getTimeInMillis())
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

							}
						});
			}
		}
	}

	public void cancelTask(String key) {
		Pair pair = taskMap.get(key);
		if (pair.uploadFileRequest != null)
			pair.uploadFileRequest.cancel(true);
	}

	private void setUploadRequest(final String key, long uploadedSize) {
		Pair pair = taskMap.get(key);
		pair.uploadFileRequest = new UploadFileRequest<UploadFileResponse>();
		pair.uploadFileRequest.addFile(pair.video.getOriginalName(),
				"video/mp4", pair.video.getOriginalPath(), uploadedSize);
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

					}

					@Override
					public void handleResponse(UploadFileResponse response) {

					}
				});

	}

	public class Pair {
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
	}
}
