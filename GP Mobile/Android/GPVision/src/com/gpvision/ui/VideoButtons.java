package com.gpvision.ui;

import com.gpvision.R;
import com.gpvision.datamodel.Video;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

public class VideoButtons extends LinearLayout {

	public VideoButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoButtons(Context context) {
		super(context);
	}

	public void setVideo(Video video) {
		switch (video.getStatus()) {
		case Uploading:
			getUploadingButtons();
			break;
		case Uploaded:
			getUploadedButtons();
			break;
		case Deleted:
			getDeleteedButtons();
			break;
		default:
			break;
		}

	}

	private void getUploadingButtons() {
		Button stopButton = new Button(getContext());
		stopButton.setBackgroundResource(R.drawable.icon_button_upload_stop);
		Button uploadButton = new Button(getContext());
		uploadButton.setBackgroundResource(R.drawable.icon_button_upload);
		addView(stopButton);
		addView(uploadButton);
	}

	private void getUploadedButtons() {
		Button playButton = new Button(getContext());
		playButton.setBackgroundResource(R.drawable.icon_button_play);
		addView(playButton);
	}

	private void getDeleteedButtons() {
		Button deletedButton = new Button(getContext());
		deletedButton.setBackgroundResource(R.drawable.icon_button_delete);
		addView(deletedButton);
	}
}
