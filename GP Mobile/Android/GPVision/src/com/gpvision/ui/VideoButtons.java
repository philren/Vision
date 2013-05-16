package com.gpvision.ui;

import com.gpvision.R;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.DeleteVideoRequest;
import com.gpvision.api.response.DeleteVideoResponse;
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
	private int mPosition;
	private Video mVideo;
	private VideoStatusChangedListener listener;
	private LayoutParams layoutParams = new LayoutParams(getResources()
			.getDimensionPixelSize(R.dimen.button_size), getResources()
			.getDimensionPixelSize(R.dimen.button_size));

	public void setPosition(int position) {
		this.mPosition = position;
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
		if (video == null)
			return;
		this.mVideo = video;
		switch (mVideo.getStatus()) {
		case uploading:
			getUploadingButtons();
			break;
		case paused:
			getPausedButtons();
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
		abortButton.setLayoutParams(layoutParams);
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button pauseButton = new Button(getContext());
		pauseButton.setLayoutParams(layoutParams);
		pauseButton.setBackgroundResource(R.drawable.icon_button_pause);
		pauseButton.setOnClickListener(pauseListener);
		abortButton.setOnClickListener(abortListener);
		addView(abortButton);
		addView(pauseButton);

	}

	private void getPausedButtons() {
		removeAllViews();
		Button abortButton = new Button(getContext());
		abortButton.setLayoutParams(layoutParams);
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button resumeButton = new Button(getContext());
		resumeButton.setLayoutParams(layoutParams);
		resumeButton.setBackgroundResource(R.drawable.icon_button_upload);
		resumeButton.setOnClickListener(resumeListener);
		abortButton.setOnClickListener(abortListener);
		addView(abortButton);
		addView(resumeButton);
	}

	private void getAnalysedButtons() {
		removeAllViews();
		Button playButton = new Button(getContext());
		playButton.setLayoutParams(layoutParams);
		playButton.setBackgroundResource(R.drawable.icon_button_play);
		Button deletedButton = new Button(getContext());
		deletedButton.setLayoutParams(layoutParams);
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		playButton.setOnClickListener(playListener);
		deletedButton.setOnClickListener(deletedListener);
		addView(playButton);
		addView(deletedButton);
	}

	private void getDeletedButtons() {
		removeAllViews();
		Button deletedButton = new Button(getContext());
		deletedButton.setLayoutParams(layoutParams);
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(deletedListener);
		addView(deletedButton);
	}

	public void deleteVideo() {
		if (!AppUtils.isEmpty(mVideo.getUuid())) {
			new DeleteVideoRequest(mVideo.getUuid())
					.start(new APIResponseHandler<DeleteVideoResponse>() {

						@Override
						public void handleError(Long errorCode,
								String errorMessage) {
							LogUtil.logE(errorMessage);
						}

						@Override
						public void handleResponse(DeleteVideoResponse response) {
							listener.delete(mPosition, mVideo);
						}
					});
		} else {
			listener.delete(mPosition, mVideo);
		}
	}

	private OnClickListener deletedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
			dialog.setTitle(R.string.video_info_fragment_alert_dialog_title_warning);
			String message = getResources().getString(
					R.string.video_info_fragment_alert_dialog_message_delete);
			dialog.setMessage(String.format(message, mVideo.getOriginalName()));
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
			dialog.setMessage(String.format(message, mVideo.getOriginalName()));
			dialog.setPositiveButton(R.string.base_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mVideo.setStatus(Status.failed);
							listener.onAbort(mPosition, mVideo);
						}
					});
			dialog.setNegativeButton(R.string.base_cancel, null);
			dialog.create().show();

		}
	};

	private OnClickListener pauseListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mVideo.setStatus(Status.paused);
			listener.onPaused(mPosition, mVideo);
		}
	};

	private OnClickListener resumeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mVideo.setStatus(Status.uploading);
			listener.onUploading(mPosition, mVideo);
		}
	};

	private OnClickListener playListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener != null)
				listener.onPlay(mVideo);
		}
	};

	public interface VideoStatusChangedListener {

		public void onChanged(int position, Video video);

		public void delete(int position, Video video);

		public void onPlay(Video video);

		public void onUploading(int position, Video video);

		public void onPaused(int position, Video video);

		public void onAbort(int position, Video video);
	}
}
