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
        if(fileNames==null)
            return;
        int size = fileNames.size();
        for (int i = 0; i < size; i++) {
            if (images.size() < size) {
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
            GalleryImage image = (GalleryImage)v;
            String fileName = image.getFileName();
            onItemClickListener.onItemClickListener(fileName);
        }
    };

    public interface OnItemClickListener {
        public void onItemClickListener(String fileName);
    }


}
