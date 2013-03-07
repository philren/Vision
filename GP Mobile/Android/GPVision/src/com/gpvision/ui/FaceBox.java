package com.gpvision.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FaceBox extends ImageView {

	private ArrayList<Rect> mRects;
	private Paint mPaint;

	public FaceBox(Context context) {
		this(context, null);
	}

	public FaceBox(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FaceBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mRects = new ArrayList<Rect>();

		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(5);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		int w = right - left;
		int h = bottom - top;

		addArea(getWidth() / 4, h / 4, 3 * w / 4, 3 * h / 4);

		super.onLayout(changed, left, top, right, bottom);
	}

	public void addArea(int l, int t, int r, int b) {
		mRects.add(new Rect(l, t, r, b));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		for (Rect rect : mRects) {
			canvas.drawRect(rect.left, rect.top, rect.right + 1, rect.top + 2,
					mPaint);
			canvas.drawRect(rect.left, rect.top + 2, rect.left + 2,
					rect.bottom - 1, mPaint);
			canvas.drawRect(rect.right - 1, rect.top, rect.right + 1,
					rect.bottom - 1, mPaint);
			canvas.drawRect(rect.left, rect.bottom - 1, rect.right + 1,
					rect.bottom + 1, mPaint);
		}

		super.onDraw(canvas);
	}
}
