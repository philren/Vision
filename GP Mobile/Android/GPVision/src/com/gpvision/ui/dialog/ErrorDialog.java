package com.gpvision.ui.dialog;

import com.gpvision.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class ErrorDialog extends AlertDialog.Builder {

	private int titleId, messageId;

	public ErrorDialog(Context context, int titleId, int messageId) {
		this(context, titleId, messageId, null);
	}

	public ErrorDialog(Context context, int titleId, int messageId,
			OnClickListener listener) {
		super(context);
		this.titleId = titleId;
		this.messageId = messageId;
		init(listener);
	}

	private void init(OnClickListener listener) {
		setTitle(titleId);
		setMessage(messageId);
		setPositiveButton(R.string.base_ok, listener);
		show();
	}
}
