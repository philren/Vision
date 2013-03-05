package com.gpvision.ui;

import com.gpvision.R;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog extends ProgressDialog {

	private LoadingDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	private LoadingDialog(Context context) {
		super(context);
		init();
	}

	private void init() {
		setMessage(getContext().getString(R.string.dialog_message_loading));
	}

}
