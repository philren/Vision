package com.gpvision.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.gpvision.adapter.ImageAdapter;

import java.util.ArrayList;

public class MyGallery extends HorizontalScrollView {

    private LinearLayout mGalleryLayout;
    private ArrayList<GalleryImage> images;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
        addView(mGalleryLayout);
    }

    public void setImageFileNames(ArrayList<String> fileNames) {
        int size = fileNames.size();
        for (int i = 0; i < size; i++) {
            if (images.size() <= 0) {
                GalleryImage image = new GalleryImage(getContext());
                image.setOnClickListener(listener);
                images.add(i, image);
            }
            images.get(i).setFileName(fileNames.get(i));
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

    public interface OnItemClickListener {
        public void onItemCilckListener(int position);
    }

    public class GalleryAdapterView extends AdapterView<ImageAdapter> {
        private ImageAdapter mAdapter;
        public GalleryAdapterView(Context context) {
            super(context);
        }

        public GalleryAdapterView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public GalleryAdapterView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public ImageAdapter getAdapter() {
            return mAdapter;
        }

        @Override
        public void setAdapter(ImageAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public View getSelectedView() {
            return null;
        }

        @Override
        public void setSelection(int position) {

        }

    }
}
