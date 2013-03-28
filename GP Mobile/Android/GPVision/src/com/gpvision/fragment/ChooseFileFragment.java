package com.gpvision.fragment;

import java.io.File;

import com.gpvision.R;
import com.gpvision.utils.AppUtils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChooseFileFragment extends BaseFragment {

	public static final String TAG = ChooseFileFragment.class.getName();
	private static final int REQUEST_CODE_CHOOSE_FILE = 2030;
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 1024;
	private File file;
	private OnChoseListener listener;
	private TextView fileInfo;

	public void setOnChoseListener(OnChoseListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_choose_file, container,
				false);
		View chooseFile = view
				.findViewById(R.id.choose_file_fragment_choose_file);
		chooseFile.setOnClickListener(this);
		fileInfo = (TextView) view
				.findViewById(R.id.choose_file_fragment_file_info);
		view.findViewById(R.id.choose_file_fragment_upload_btn)
				.setOnClickListener(this);
		view.findViewById(R.id.choose_file_fragment_cancel_btn)
				.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CHOOSE_FILE
				&& resultCode == FragmentActivity.RESULT_OK) {
			Uri uri = data.getData();
			Cursor cursor = getActivity().getContentResolver().query(uri, null,
					null, null, null);
			if (cursor == null || cursor.getCount() == 0)
				return;
			cursor.moveToFirst();
			String choseFilePath = null;
			try {
				choseFilePath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
				cursor.close();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			file = new File(choseFilePath);
			// TODO add check
			if (file.exists()) {
				fileInfo.setText(file.getName() + "\n size:" + file.length());
				if (file.length() > MAX_FILE_SIZE) {
					fileInfo.append(getString(R.string.choose_file_fragment_file_big));
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_file_fragment_choose_file:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("video/*");
			startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
			break;
		case R.id.choose_file_fragment_upload_btn:
			if (listener != null && file != null && file.exists()) {
				if (file.length() > MAX_FILE_SIZE) {
					AppUtils.toastLong(getActivity(),
							R.string.choose_file_fragment_file_big);
					break;
				}
				listener.onChose(file);
				getFragmentManager().popBackStack();
			}
			break;
		case R.id.choose_file_fragment_cancel_btn:
			getFragmentManager().popBackStack();
			break;
		default:
			break;
		}
	}

	public interface OnChoseListener {
		public void onChose(File file);
	}
}
