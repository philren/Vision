package com.gpvision.ui.dialog;

import com.gpvision.R;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog extends ProgressDialog {

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public LoadingDialog(Context context) {
		super(context);
		init();
	}

	private void init() {
		setMessage(getContext().getString(R.string.dialog_message_loading));
	}

}
