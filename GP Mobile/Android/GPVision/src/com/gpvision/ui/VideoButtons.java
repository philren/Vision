package com.gpvision.ui;

import java.util.Calendar;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.DeleteVideoRequest;
import com.gpvision.api.request.GetUploadedSizeRequest;
import com.gpvision.api.request.UploadFileRequest;
import com.gpvision.api.request.UploadFileRequest.UploadedProgressCallback;
import com.gpvision.api.response.DeleteVideoResponse;
import com.gpvision.api.response.GetUploadedSizeResponse;
import com.gpvision.api.response.UploadFileResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LogUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class VideoButtons extends LinearLayout {
	private int position;
	private Video video;
	private VideoStatusChangedListener listener;
	private UploadFileRequest<UploadFileResponse> request;
	private Status lastStatus = Status.unknow;

	public void setPosition(int position) {
		if (this.position != position)
			lastStatus = Status.unknow;
		this.position = position;
	}

	public void setListener(VideoStatusChangedListener listener) {
		this.listener = listener;
	}

	public VideoButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoButtons(Context context) {
		super(context);
	}

	public void setVideo(Video video) {
		if (video.getStatus() == null) {
			return;
		}
		this.video = video;
		switch (video.getStatus()) {
		case uploading:
			if (lastStatus != Status.uploading) {
				getUploadingButtons();
				lastStatus = Status.uploading;
			}
			break;
		case paused:
			if (lastStatus != Status.paused) {
				getPausedButtons();
				lastStatus = Status.paused;
			}
			break;
		case indexed:
		case uploaded:
		case failed:
		case wait_index:
			getDeletedButtons();
			break;
		case analysed:
			getAnalysedButtons();
			break;
		case indexing:
		case encoding:
		case analysing:
			removeAllViews();
			break;
		default:
			removeAllViews();
			break;
		}

	}

	private void getUploadingButtons() {
		removeAllViews();
		Button abortButton = new Button(getContext());
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button pauseButton = new Button(getContext());
		pauseButton.setBackgroundResource(R.drawable.icon_button_pause);
		pauseButton.setOnClickListener(pauseListener);
		abortButton.setOnClickListener(abortListener);
		addView(abortButton);
		addView(pauseButton);
		// start uploading task
		new GetUploadedSizeRequest(video.getOriginalName(),
				AppUtils.getMd5(video.getOriginalPath()), video.getVideoSize(),
				Calendar.getInstance().getTimeInMillis())
				.start(new APIResponseHandler<GetUploadedSizeResponse>() {

					@Override
					public void handleError(Long errorCode, String errorMessage) {

					}

					@Override
					public void handleResponse(GetUploadedSizeResponse response) {
						long uploadedSize = response.getUploadedSize();
						request = new UploadFileRequest<UploadFileResponse>();
						request.addFile(video.getOriginalName(), "video/mp4",
								video.getOriginalPath(), uploadedSize);
						request.setCallback(uploadedProgressCallback);
						request.start(new APIResponseHandler<UploadFileResponse>() {

							@Override
							public void handleResponse(
									UploadFileResponse response) {

							}

							@Override
							public void handleError(Long errorCode,
									String errorMessage) {

							}
						});
					}
				});

	}

	private void getPausedButtons() {
		removeAllViews();
		Button abortButton = new Button(getContext());
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button resumeButton = new Button(getContext());
		resumeButton.setBackgroundResource(R.drawable.icon_button_upload);
		resumeButton.setOnClickListener(resumeListener);
		abortButton.setOnClickListener(abortListener);
		addView(abortButton);
		addView(resumeButton);
		// stop uploading task
		if (request != null) {
			request.cancel(true);
			request = null;
		}
	}

	private void getAnalysedButtons() {
		removeAllViews();
		Button playButton = new Button(getContext());
		playButton.setBackgroundResource(R.drawable.icon_button_play);
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		playButton.setOnClickListener(playListener);
		deletedButton.setOnClickListener(deletedListener);
		addView(playButton);
		addView(deletedButton);
	}

	private void getDeletedButtons() {
		removeAllViews();
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(deletedListener);
		addView(deletedButton);
	}

	// private void getFailedButtons() {
	// removeAllViews();
	// Button deletedButton = new Button(getContext());
	// deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
	// deletedButton.setOnClickListener(deletedListener);
	// addView(deletedButton);
	// }

	public void deleteVideo() {
		new DeleteVideoRequest(video.getUuid())
				.start(new APIResponseHandler<DeleteVideoResponse>() {

					@Override
					public void handleError(Long errorCode, String errorMessage) {
						LogUtil.logE(errorMessage);
					}

					@Override
					public void handleResponse(DeleteVideoResponse response) {
						listener.delete(position);
					}
				});
	}

	private OnClickListener deletedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
			dialog.setTitle(R.string.video_info_fragment_alert_dialog_title_warning);
			String message = getResources().getString(
					R.string.video_info_fragment_alert_dialog_message_delete);
			dialog.setMessage(String.format(message, video.getOriginalName()));
			dialog.setPositiveButton(R.string.base_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteVideo();
						}
					});
			dialog.setNegativeButton(R.string.base_cancel, null);
			dialog.create().show();
		}
	};

	private OnClickListener abortListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
			dialog.setTitle(R.string.video_info_fragment_alert_dialog_title_warning);
			String message = getResources().getString(
					R.string.video_info_fragment_alert_dialog_message_abort);
			dialog.setMessage(String.format(message, video.getOriginalName()));
			dialog.setPositiveButton(R.string.base_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							video.setStatus(Status.failed);
							listener.onChanged(position, video);
						}
					});
			dialog.setNegativeButton(R.string.base_cancel, null);
			dialog.create().show();

		}
	};

	private OnClickListener pauseListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			video.setStatus(Status.paused);
			listener.onChanged(position, video);
		}
	};
	private OnClickListener resumeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			video.setStatus(Status.uploading);
			listener.onChanged(position, video);
		}
	};

	private OnClickListener playListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener != null)
				listener.onPlay(video);
		}
	};

	private UploadedProgressCallback uploadedProgressCallback = new UploadedProgressCallback() {

		@Override
		public void uploadedProgress(long uploadedBytes) {
			video.setUploadedSize(uploadedBytes);
			listener.onChanged(position, video);
		}
	};

	public interface VideoStatusChangedListener {

		public void onChanged(int position, Video video);

		public void delete(int position);

		public void onPlay(Video video);
	}
}
