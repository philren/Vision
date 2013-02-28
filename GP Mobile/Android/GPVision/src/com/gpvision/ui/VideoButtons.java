package com.gpvision.ui;

import com.gpvision.R;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class VideoButtons extends LinearLayout {
	private Video video;
	private VideoStatusChangedListener listener;

	public VideoButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoButtons(Context context) {
		super(context);
	}

	public void setVideo(Video video, VideoStatusChangedListener listener) {
		this.video = video;
		this.listener = listener;
		removeAllViews();
		switch (video.getStatus()) {
		case Uploading:
			getUploadingButtons();
			break;
		case Paused:
			getPausedButtons();
			break;
		case Indexed:
			getIndexedButtons();
			break;
		case Deleted:
			getDeletedButtons();
			break;
		case Failed:
			getFailedButtons();
			break;
		default:
			break;
		}

	}

	private void getUploadingButtons() {
		Button abortButton = new Button(getContext());
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button pauseButton = new Button(getContext());
		pauseButton.setBackgroundResource(R.drawable.icon_button_pause);
		pauseButton.setOnClickListener(pauseListener);
		abortButton.setOnClickListener(abortListener);
		addView(abortButton);
		addView(pauseButton);
	}

	private void getPausedButtons() {
		Button abortButton = new Button(getContext());
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button resumeButton = new Button(getContext());
		resumeButton.setBackgroundResource(R.drawable.icon_button_upload);
		resumeButton.setOnClickListener(resumeListener);
		abortButton.setOnClickListener(abortListener);
		addView(abortButton);
		addView(resumeButton);
	}

	private void getIndexedButtons() {
		Button playButton = new Button(getContext());
		playButton.setBackgroundResource(R.drawable.icon_button_play);
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(deletedListener);
		addView(playButton);
		addView(deletedButton);
	}

	private void getDeletedButtons() {
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(deletedListener);
		addView(deletedButton);
	}

	private void getFailedButtons() {
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
			dialog.setMessage(String.format(message, video.getName()));
			dialog.setPositiveButton(R.string.base_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							listener.remove(video);
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
			dialog.setMessage(String.format(message, video.getName()));
			dialog.setPositiveButton(R.string.base_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							video.setStatus(Status.Failed);
							listener.statusChanged();
						}
					});
			dialog.setNegativeButton(R.string.base_cancel, null);
			dialog.create().show();

		}
	};

	private OnClickListener pauseListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			video.setStatus(Status.Paused);
			listener.statusChanged();
		}
	};
	private OnClickListener resumeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			video.setStatus(Status.Uploading);
			listener.statusChanged();
		}
	};

	public interface VideoStatusChangedListener {
		public void statusChanged();

		public void remove(Video video);
	}
}
