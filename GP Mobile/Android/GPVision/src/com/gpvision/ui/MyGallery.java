package com.gpvision.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class MyGallery extends HorizontalScrollView {

	private LinearLayout mGalleryLayout;
	private ArrayList<GalleryImage> images;
	private OnItemClickListener onItemClickListener;
	private boolean touchable = false;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setTouchable(boolean touchable) {
		this.touchable = touchable;
	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyGallery(Context context) {
		super(context);
		init();
	}

	private void init() {
		images = new ArrayList<GalleryImage>();
		mGalleryLayout = new LinearLayout(getContext());
		mGalleryLayout.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		addView(mGalleryLayout);
	}

	public void setImageChildDirs(ArrayList<String> childDirs) {
		if (childDirs == null)
			return;
		mGalleryLayout.removeAllViews();
		int size = childDirs.size();
		for (int i = 0; i < size; i++) {
			if (i == images.size()) {
				GalleryImage mImage = new GalleryImage(getContext());
				mImage.setOnClickListener(listener);
				images.add(i, mImage);
			}
			images.get(i).setChildDir(childDirs.get(i));
			mGalleryLayout.addView(images.get(i));
		}

	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GalleryImage image = (GalleryImage) v;
			String childDir = image.getChildDir();
			if (onItemClickListener != null)
				onItemClickListener.onItemClickListener(childDir);
		}
	};

	public interface OnItemClickListener {
		public void onItemClickListener(String childDir);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (touchable)
			return super.onTouchEvent(ev);
		else
			return false;
	}

}
