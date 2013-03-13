package com.gpvision.ui;

import com.gpvision.utils.ImageCacheUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GalleryImage extends ImageView {

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setImage();
	}

	public GalleryImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GalleryImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GalleryImage(Context context) {
		super(context);
	}

	private void setImage() {
		Bitmap bitmap = ImageCacheUtil.getBitmapFromFile(fileName, 50, 50);
		if (bitmap != null)
			setImageBitmap(bitmap);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, heightMeasureSpec);
	}
	
}
