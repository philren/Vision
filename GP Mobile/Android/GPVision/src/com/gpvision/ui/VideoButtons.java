package com.gpvision.ui;

import com.gpvision.R;
import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;

import android.content.Context;
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
			getDeleteedButtons();
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
		pauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				video.setStatus(Status.Paused);
				listener.statusChanged(video);
			}
		});
		abortButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				video.setStatus(Status.Failed);
				listener.statusChanged(video);
			}
		});
		addView(abortButton);
		addView(pauseButton);
	}

	private void getPausedButtons() {
		Button abortButton = new Button(getContext());
		abortButton.setBackgroundResource(R.drawable.icon_button_abort);
		Button resumeButton = new Button(getContext());
		resumeButton.setBackgroundResource(R.drawable.icon_button_upload);
		resumeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				video.setStatus(Status.Uploading);
				listener.statusChanged(video);
			}
		});
		abortButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				video.setStatus(Status.Failed);
				listener.statusChanged(video);
			}
		});
		addView(abortButton);
		addView(resumeButton);
	}

	private void getIndexedButtons() {
		Button playButton = new Button(getContext());
		playButton.setBackgroundResource(R.drawable.icon_button_play);
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.remove(video);
			}
		});
		addView(playButton);
		addView(deletedButton);
	}

	private void getDeleteedButtons() {
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.remove(video);
			}
		});
		addView(deletedButton);
	}

	private void getFailedButtons() {
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		deletedButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.remove(video);
			}
		});
		addView(deletedButton);
	}

	public interface VideoStatusChangedListener {
		public void statusChanged(Video video);

		public void remove(Video video);
	}
}
