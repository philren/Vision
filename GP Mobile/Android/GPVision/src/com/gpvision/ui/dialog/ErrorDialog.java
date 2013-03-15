package com.gpvision.ui.dialog;

import com.gpvision.R;

import android.app.AlertDialog;
import android.content.Context;

public class ErrorDialog extends AlertDialog.Builder {

	private int titleId, messageId;

	private ErrorDialog(Context context) {
		super(context);
		init();
	}

	public ErrorDialog(Context context, int titleId, int messageId) {
		super(context);
		this.titleId = titleId;
		this.messageId = messageId;
		init();
	}

	private void init() {
		setTitle(titleId);
		setMessage(messageId);
		setPositiveButton(R.string.base_ok, null);
		show();
	}
}
