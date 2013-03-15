package com.gpvision.ui.dialog;

import com.gpvision.R;

import android.app.ProgressDialog;
import android.content.Context;

public class LoginDialog extends ProgressDialog {

	public LoginDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public LoginDialog(Context context) {
		super(context);
		init();
	}

	private void init() {
		setMessage(getContext().getString(R.string.dialog_message_login));
	}
}
