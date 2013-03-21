package com.gpvision.ui;

import com.gpvision.utils.ImageCacheUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class GalleryImage extends ImageView {

	private static final LayoutParams PARAMS = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
	private String childDir;

	public String getChildDir() {
		return childDir;
	}

	public void setChildDir(String childDir) {
		this.childDir = childDir;
		setImage();
	}

	public GalleryImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GalleryImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GalleryImage(Context context) {
		super(context);
		init();
	}

	private void init() {
		setLayoutParams(PARAMS);
	}

	private void setImage() {
		Bitmap bitmap = ImageCacheUtil.getBitmapFromFile(childDir, 50, 50);
		if (bitmap != null)
			setImageBitmap(bitmap);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, heightMeasureSpec);
	}

}
