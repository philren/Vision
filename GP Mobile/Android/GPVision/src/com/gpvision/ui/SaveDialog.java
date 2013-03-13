package com.gpvision.ui;

import java.io.File;

import com.gpvision.R;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.ImageCacheUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public class SaveDialog extends AlertDialog.Builder {

	private EditText editText;
	private Context context;
	private String fileName;

	private SaveDialog(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public SaveDialog(Context context, String fileName) {
		this(context);
		this.fileName = fileName;
	}

	private void init() {
		editText = new EditText(context);
		editText.setText(ImageCacheUtil.SAVE_DIR);
		setView(editText);
		setPositiveButton(R.string.base_ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String root = editText.getText().toString().trim();
				if (root.lastIndexOf("/") - 1 != root.length()) {
					root = root + File.separator;
				}
				ImageCacheUtil.saveTo(fileName, root + fileName);
				if (new File(root + fileName).exists()) {
					AppUtils.toastLong(context,
							R.string.save_and_share_fragment_save_success);
				} else {
					AppUtils.toastLong(context,
							R.string.save_and_share_fragment_save_failed);
				}
			}
		});
		setNegativeButton(R.string.base_cancel, null);
		setTitle(R.string.save_and_share_fragment_save_dialog_title);
		show();
	}
}
