package com.gpvision.fragment;

import java.io.File;

import com.gpvision.R;
import com.gpvision.ui.dialog.SaveDialog;
import com.gpvision.utils.ImageCacheUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SaveAndShareFragment extends BaseFragment {

	public static final String TAG = SaveAndShareFragment.class.getName();
	private String childDir;

	public void setChildDir(String childDir) {
		this.childDir = childDir;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_save_and_share,
				container, false);
		TextView imageTitle = (TextView) view
				.findViewById(R.id.save_and_share_fragment_image_name_title);
		imageTitle.setText(ImageCacheUtil.getFileName(childDir));
		ImageView imageView = (ImageView) view
				.findViewById(R.id.save_and_share_fragment_image_view);
		imageView.setImageBitmap(ImageCacheUtil.getBitmapFromFile(childDir,
				400, 400));

		view.findViewById(R.id.save_and_share_fragment_save_btn)
				.setOnClickListener(this);
		view.findViewById(R.id.save_and_share_fragment_share_btn)
				.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_and_share_fragment_save_btn:
			new SaveDialog(getActivity(), childDir);
			break;
		case R.id.save_and_share_fragment_share_btn:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			File file = new File(ImageCacheUtil.CACHE_DIR + childDir);
			shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			shareIntent.setType("image/jpeg");
			startActivity(Intent
					.createChooser(
							shareIntent,
							getString(R.string.save_and_share_fragment_share_button_text)));
			break;

		default:
			break;
		}
	}

}
