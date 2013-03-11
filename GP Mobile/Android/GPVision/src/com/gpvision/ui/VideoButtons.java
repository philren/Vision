package com.gpvision.ui;

import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.UploadFileRequest;
import com.gpvision.api.request.UploadFileRequest.UploadedProgressCallback;
import com.gpvision.api.response.UploadFileResponse;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.fragment.VideoPlayFragment;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
			if (lastStatus != Status.uploaded) {
				getUploadingButtons();
				lastStatus = Status.uploaded;
			}
			break;
		case paused:
			if (lastStatus != Status.paused) {
				getPausedButtons();
				lastStatus = Status.paused;
			}
			break;
		case indexed:
			if (lastStatus != Status.indexed) {
				getIndexedButtons();
				lastStatus = Status.indexed;
			}
			break;
		case deleted:
			if (lastStatus != Status.deleted) {
				getDeletedButtons();
				lastStatus = Status.deleted;
			}
			break;
		case failed:
			if (lastStatus != Status.failed) {
				getFailedButtons();
				lastStatus = Status.failed;
			}
			break;
		default:
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
		request.addFile(video.getOriginalName(), "video/mp4",
				video.getOriginalPath());
		request.setCallback(new UploadedProgressCallback() {

			@Override
			public void uploadedProgress(long uploadedBytes) {
				video.setUploadedLength(uploadedBytes);
			}
		});
		request.start(new APIResponseHandler<UploadFileResponse>() {

			@Override
			public void handleResponse(UploadFileResponse response) {

			}

			@Override
			public void handleError(Long errorCode, String errorMessage) {

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

	private void getIndexedButtons() {
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

	private void getFailedButtons() {
		removeAllViews();
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(deletedListener);
		addView(deletedButton);
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
							listener.remove(position);
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
							listener.onChanged();
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
			listener.onChanged();
		}
	};
	private OnClickListener resumeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			video.setStatus(Status.uploading);
			listener.onChanged();
		}
	};

	private OnClickListener playListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			VideoPlayFragment fragment = new VideoPlayFragment();
			Bundle args = new Bundle();
			args.putParcelable(VideoPlayFragment.ARGS_VIDEO_KEY, video);
			fragment.setArguments(args);
			MessageCenter.getInstance()
					.sendMessage(
							new Message(MainActivity.MESSAGE_UPDATE_FRAGMENT,
									fragment));
		}
	};

	public interface VideoStatusChangedListener {

		public void onChanged();

		public void remove(int position);
	}
}
